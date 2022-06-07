package ru.neoflex.credit.deal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.deal.feign.ConveyorFeign;
import ru.neoflex.credit.deal.model.*;
import ru.neoflex.credit.deal.repository.ApplicationRepository;
import ru.neoflex.credit.deal.repository.ClientRepository;
import ru.neoflex.credit.deal.repository.CreditRepository;
import ru.neoflex.credit.deal.service.abstracts.DealService;

import static ru.neoflex.credit.deal.model.ApplicationStatusEnum.*;
import static ru.neoflex.credit.deal.model.ApplicationStatusHistoryDTO.ChangeTypeEnum.*;
import static ru.neoflex.credit.deal.model.ApplicationStatusHistoryDTO.ChangeTypeEnum;
import static ru.neoflex.credit.deal.model.CreditStatus.CALCULATED;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {
    private final ApplicationRepository applicationRepository;
    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    private final ConveyorFeign conveyorFeignClient;
    @Override
    public List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO request) {
        log.info("Start create application");

        Client clientObject = createClientByRequest(request);
        log.debug("client = {}", clientObject);

        Client clientBD = clientRepository.save(clientObject);
        List<ApplicationStatusHistoryDTO> statusHistoryList = List.of(
                new ApplicationStatusHistoryDTO()
                        .status(PREAPPROVAL)
                        .time(LocalDateTime.now())
                        .changeType(AUTOMATIC)
        );
        log.debug("List of application history {}", statusHistoryList);

        Application applicationObject = new Application()
                .client(clientBD)
                .creationDate(LocalDate.now())
                .status(PREAPPROVAL)
                .statusHistory(statusHistoryList);
        Application applicationBD = applicationRepository.save(applicationObject);
        clientRepository.save(clientBD.setApplication(applicationBD));
        log.debug("application = {}", applicationBD);

        List<LoanOfferDTO> offersList = conveyorFeignClient.createOffers(request).getBody();

        if (offersList != null && offersList.size() > 0) {
            offersList.forEach(offer -> offer.setApplicationId(applicationBD.id()));
            offersList.sort(Comparator.comparing(LoanOfferDTO::getRate));
        }
        log.info("End. Offers list: {}", offersList);

        return offersList;
    }

    @Override
    public void offer(LoanOfferDTO loanOfferDTO) {
        Application application = applicationRepository.getReferenceById(loanOfferDTO.getApplicationId());
        List<ApplicationStatusHistoryDTO> applicationStatusHistoryList = updateStatusHistory(
                application.statusHistory(), APPROVED, AUTOMATIC
        );
        application.appliedOffer(loanOfferDTO);
        application.status(APPROVED);
        application.statusHistory(applicationStatusHistoryList);
        Application applicationUpdate = applicationRepository.save(application);
        log.info("applicationUpdate = {}", applicationUpdate);
    }

    @Override
    public void calculateCredit(Long applicationId, ScoringDataDTO scoringDataDTO) {
        log.info("Start calculate credit");
        Application application = applicationRepository.getReferenceById(applicationId);
        log.debug("application = {}", application);

        Client client = application.client();
        log.debug("client = {}", client);

        LoanOfferDTO offer = application.appliedOffer();
        log.debug("offer = {}", offer);

        scoringDataDTO
                .amount(offer.getTotalAmount())
                .term(offer.getTerm())
                .lastName(client.getLastName())
                .firstName(client.getFirstName())
                .middleName(client.getMiddleName())
                .birthdate(client.getBirthDate())
                .passportSeries(client.getPassport().getSeries())
                .passportNumber(client.getPassport().getNumber())
                .isInsuranceEnabled(offer.getIsInsuranceEnabled())
                .isSalaryClient(offer.getIsSalaryClient());
        log.debug("scoringDataDTO = {}", scoringDataDTO);

        CreditDTO creditDTO = conveyorFeignClient.scoring(scoringDataDTO).getBody();
        log.debug("creditDTO = {}", creditDTO);

        assert creditDTO != null;
        Credit credit = creditRepository.save(new Credit()
                        .setAmount(creditDTO.getAmount())
                        .setTerm(creditDTO.getTerm())
                        .setMonthlyPayment(creditDTO.getMonthlyPayment())
                        .setRate(creditDTO.getRate())
                        .setPsk(creditDTO.getPsk())
                        .setPaymentSchedule(creditDTO.getPaymentSchedule())
                        .setIsInsuranceEnabled(creditDTO.getIsInsuranceEnabled())
                        .setIsSalaryClient(creditDTO.getIsSalaryClient())
                        .setClient(client)
                        .setApplication(application)
                        .setCreditStatus(CALCULATED)
                );
        log.debug("credit = {}", credit);

        Passport passportInformation = new Passport()
                .series(client.getPassport().getSeries())
                .number(client.getPassport().getNumber())
                .issueDate(scoringDataDTO.getPassportIssueDate())
                .issueBranch(scoringDataDTO.getPassportIssueBranch());

        clientRepository.save(client
                .setGender(scoringDataDTO.getGender().name())
                .setPassport(passportInformation)
                .setMartialStatus(scoringDataDTO.getMaritalStatus().name())
                .setDependentAmount(scoringDataDTO.getDependentAmount())
                .setEmploymentDTO(scoringDataDTO.getEmployment())
                .setAccount(scoringDataDTO.getAccount())
                .setCredit(credit));

        List<ApplicationStatusHistoryDTO> updatedStatusHistory = updateStatusHistory(application.statusHistory(), CC_APPROVED, AUTOMATIC);

        applicationRepository.save(application
                .status(CC_APPROVED)
                .statusHistory(updatedStatusHistory)
                .credit(credit));
    }

    private List<ApplicationStatusHistoryDTO> updateStatusHistory(
            List<ApplicationStatusHistoryDTO> statusHistoryList, ApplicationStatusEnum status, ChangeTypeEnum changeTyp) {
        statusHistoryList.add(
                new ApplicationStatusHistoryDTO()
                        .status(status)
                        .time(LocalDateTime.now())
                        .changeType(changeTyp)
        );
        return statusHistoryList;
    }

    private Client createClientByRequest(LoanApplicationRequestDTO request) {
        Passport passportClient = new Passport()
                .series(request.getPassportSeries())
                .number(request.getPassportNumber());

        return new Client()
                .setLastName(request.getLastName())
                .setFirstName(request.getFirstName())
                .setMiddleName(request.getMiddleName())
                .setBirthDate(request.getBirthdate())
                .setEmail(request.getEmail())
                .setPassport(passportClient);
    }
}
