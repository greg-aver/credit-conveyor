package ru.neoflex.credit.deal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.deal.exception.InappropriateStatusException;
import ru.neoflex.credit.deal.exception.SesDifferentException;
import ru.neoflex.credit.deal.model.*;
import ru.neoflex.credit.deal.repository.ApplicationRepository;
import ru.neoflex.credit.deal.repository.CreditRepository;
import ru.neoflex.credit.deal.service.abstracts.DossierService;
import ru.neoflex.credit.deal.service.abstracts.MessageService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import static ru.neoflex.credit.deal.model.ApplicationStatusEnum.*;
import static ru.neoflex.credit.deal.model.ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC;
import static ru.neoflex.credit.deal.model.CreditStatus.ISSUED;

@RequiredArgsConstructor
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {
    private final ApplicationRepository applicationRepository;
    private final CreditRepository creditRepository;
    private final DossierService dossierService;
    @Override
    public void send(Long applicationId) {
/*        Application application = validApplication(applicationId, CC_APPROVED);
        application.status(PREPARE_DOCUMENTS);*/
        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new EntityNotFoundException(String.format("Application with applicationID = %d not exists", applicationId)));
        application.statusHistory().add(
                new ApplicationStatusHistoryDTO()
                        .time(LocalDateTime.now())
                        .changeType(AUTOMATIC)
                        .status(PREPARE_DOCUMENTS)
        );
        Application applicationDB = applicationRepository.save(application
                .status(PREPARE_DOCUMENTS));
        log.info("New application = {}", applicationDB);
        dossierService.send(new EmailMessage()
                .theme(EmailMessage.ThemeEnum.SEND_DOCUMENTS)
                .address(application.client().getEmail())
                .applicationId(applicationId));
    }

    @Override
    public void createDocumentsRequest(Long applicationId) {
        Application application = validApplication(applicationId, CC_APPROVED);
        log.info("Sending create document request for application {}",
                application);

        dossierService.send(new EmailMessage()
                .theme(EmailMessage.ThemeEnum.CREATE_DOCUMENTS)
                .applicationId(applicationId)
                .address(application.client().getEmail()));
    }
    private Application validApplication(Long applicationId, ApplicationStatusEnum statusExpected) {
        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new EntityNotFoundException(String.format("Application with applicationID = %d not exists", applicationId)));
        ApplicationStatusEnum applicationStatus = application.status();
        if (applicationStatus != statusExpected) {
            throw new InappropriateStatusException(
                    String.format("Application with applicationID = %d has status = %s. Application status should be %s" , application.id(), applicationStatus, statusExpected)
            );
        }
        return application;
    }

    private Application validApplication(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Application with applicationID = %d not exists", applicationId)));
    }

    private int generateSes() {
        return ThreadLocalRandom.current().nextInt(1000, 10000);
    }
    @Override
    public void sign(Long applicationId) {
        Application application = validApplication(applicationId, DOCUMENT_CREATED);
        int ses = generateSes();
        application.sesCode(ses);
        applicationRepository.save(application);
        String emailAddress = application.client().getEmail();
        log.info(
                String.format("Sign document. Application = %s with email %s", application, emailAddress)
        );
        dossierService.send(new EmailMessage()
                .applicationId(applicationId)
                .address(emailAddress)
                .theme(EmailMessage.ThemeEnum.SEND_SES));
    }

    @Override
    public void code(Long applicationId, Integer sesActual) {
        Application application = validApplication(applicationId, DOCUMENT_CREATED);
        Integer sesExpected = application.sesCode();
        if (!sesExpected.equals(sesActual)) {
            throw new SesDifferentException(
                    String.format("Expected ses code = %d, but ses actual = %d. ApplicationId = %d", sesExpected, sesActual, applicationId)
            );
        }

        application.status(DOCUMENT_SIGNED);
        application.statusHistory().add(new ApplicationStatusHistoryDTO()
                .status(DOCUMENT_SIGNED)
                .time(LocalDateTime.now())
                .changeType(AUTOMATIC));

        applicationRepository.save(application.signDate(LocalDate.now()));

        issueCredit(applicationId);
    }


    private void issueCredit(Long applicationId) {
        Application application = validApplication(applicationId);
        Long creditId = application.credit().getId();

        Credit credit = creditRepository.getReferenceById(creditId);

        application.statusHistory().add(new ApplicationStatusHistoryDTO()
                .status(CREDIT_ISSUED)
                .time(LocalDateTime.now())
                .changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC));

        application.status(CREDIT_ISSUED);
        applicationRepository.save(application);

        creditRepository.save(credit.setCreditStatus(ISSUED));

        dossierService.send(new EmailMessage()
                .theme(EmailMessage.ThemeEnum.CREDIT_ISSUED)
                .applicationId(applicationId)
                .address(application.client().getEmail()));
    }
}
