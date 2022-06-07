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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        clientRepository.save(clientBD.application(applicationBD));
        log.debug("application = {}", applicationBD);

        List<LoanOfferDTO> offersList = conveyorFeignClient.createOffers(request).getBody();

        if (offersList != null && offersList.size() > 0) {
            offersList.forEach(offer -> offer.setApplicationId(applicationBD.id()));
            offersList.sort((o1, o2) -> o1.getRate().compareTo(o2.getRate()));
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
        Application application = applicationRepository.getReferenceById(applicationId);
        Client client = application.client();
        LoanOfferDTO offer = application.appliedOffer();
        scoringDataDTO
                .amount(offer.getTotalAmount())
                .term(offer.getTerm())
                .lastName(client.lastName())
                .firstName(client.firstName())
                .middleName(client.middleName())
                .gender(ScoringDataDTO.GenderEnum.valueOf(client.gender()))
                .birthdate(client.birthDate())
                .passportSeries(client.passport().getSeries())
                .passportNumber(client.passport().getNumber())
                .isInsuranceEnabled(offer.getIsInsuranceEnabled())
                .isSalaryClient(offer.getIsSalaryClient());
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
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .birthDate(request.getBirthdate())
                .email(request.getEmail())
                .passport(passportClient);
    }
}
