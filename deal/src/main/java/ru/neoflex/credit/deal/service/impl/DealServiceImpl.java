package ru.neoflex.credit.deal.service.impl;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {
    private final ApplicationRepository applicationRepository;
    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    private final ConveyorFeign conveyorFeignClient;
    @Override
    public List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO request) {
        Client clientObject = createClientByRequest(request);
        Client clientBD = clientRepository.save(clientObject);
        List<ApplicationStatusHistoryDTO> statusHistoryList = List.of(
                new ApplicationStatusHistoryDTO()
                        .status(PREAPPROVAL)
                        .time(LocalDateTime.now())
                        .changeType(AUTOMATIC)
        );
        Application applicationObject = new Application()
                .client(clientBD)
                .creationDate(LocalDate.now())
                .status(PREAPPROVAL)
                .statusHistory(statusHistoryList);
        Application applicationBD = applicationRepository.save(applicationObject);
        clientRepository.save(clientBD.application(applicationBD));

        List<LoanOfferDTO> offersList = conveyorFeignClient.createOffers(request).getBody();

        if (offersList.size() > 0) {
            offersList.forEach(offer -> offer.setApplicationId(applicationBD.id()));
            offersList.sort((o1, o2) -> o1.getRate().compareTo(o2.getRate()));
        }
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
    }

    @Override
    public void calculateCredit(Long applicationId, ScoringDataDTO scoringDataDTO) {

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
