package ru.neoflex.credit.deal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.neoflex.credit.deal.exception.InappropriateStatusException;
import ru.neoflex.credit.deal.model.Application;
import ru.neoflex.credit.deal.model.ApplicationStatusEnum;
import ru.neoflex.credit.deal.model.ApplicationStatusHistoryDTO;
import ru.neoflex.credit.deal.model.EmailMessage;
import ru.neoflex.credit.deal.repository.ApplicationRepository;
import ru.neoflex.credit.deal.service.abstracts.DossierService;
import ru.neoflex.credit.deal.service.abstracts.MessageService;

import javax.persistence.EntityExistsException;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import static ru.neoflex.credit.deal.model.ApplicationStatusEnum.*;
import static ru.neoflex.credit.deal.model.ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC;
import static ru.neoflex.credit.deal.model.EmailMessage.ThemeEnum.COMPLETE_DOCUMENT;
import static ru.neoflex.credit.deal.model.EmailMessage.ThemeEnum.LINK_SIGN;

@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final ApplicationRepository applicationRepository;
    private final DossierService dossierService;
    @Override
    public void send(Long applicationId) {
        Application application = applicationRepository.getReferenceById(applicationId);
        validApplication(application, CC_APPROVED);
        application.status();
        application.statusHistory().add(
                new ApplicationStatusHistoryDTO()
                        .time(LocalDateTime.now())
                        .changeType(AUTOMATIC)
                        .status(PREPARE_DOCUMENTS)
        );
        Application applicationDB = applicationRepository.save(application);
        log.info("New application = {}", applicationDB);
        dossierService.send(new EmailMessage()
                .theme(COMPLETE_DOCUMENT)
                .address(application.client().getEmail())
                .applicationId(applicationId));
    }

    private void validApplication(Application application, ApplicationStatusEnum statusExpected) {
        if (application == null) {
            throw new EntityExistsException(String.format("Application with applicationID = %d not exists", application.id()));
        }
        ApplicationStatusEnum applicationStatus = application.status();
        if (applicationStatus != statusExpected) {
            throw new InappropriateStatusException(
                    String.format("Application with applicationID = %d has status = %s. Application status should be %s" , application.id(), applicationStatus, statusExpected)
            );
        }
    }

    private int generateSes() {
        return ThreadLocalRandom.current().nextInt(1000, 10000);
    }
    @Override
    public void sign(Long applicationId) {
        Application application = applicationRepository.getReferenceById(applicationId);
        validApplication(application, DOCUMENT_CREATED);
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
                .theme(LINK_SIGN));
    }

    @Override
    public void code(Long applicationId) {

    }
}
