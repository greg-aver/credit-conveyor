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

import static ru.neoflex.credit.deal.model.ApplicationStatusEnum.CC_APPROVED;
import static ru.neoflex.credit.deal.model.ApplicationStatusEnum.PREPARE_DOCUMENTS;
import static ru.neoflex.credit.deal.model.ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC;
import static ru.neoflex.credit.deal.model.EmailMessage.ThemeEnum.COMPLETE_DOCUMENT;

@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final ApplicationRepository applicationRepository;
    private final DossierService dossierService;
    @Override
    public void send(Long applicationId) {
        Application application = applicationRepository.getReferenceById(applicationId);
        if (application == null) {
            throw new EntityExistsException(String.format("Application with applicationID = %d not exists", applicationId));
        }
        ApplicationStatusEnum applicationStatus = application.status();
        if (applicationStatus != CC_APPROVED) {
            throw new InappropriateStatusException(
                    String.format("Application with applicationID = %d has status = %s. Application status should be %s" , applicationId, applicationStatus, CC_APPROVED)
            );
        }

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

    @Override
    public void sign(Long applicationId) {

    }

    @Override
    public void code(Long applicationId) {

    }
}
