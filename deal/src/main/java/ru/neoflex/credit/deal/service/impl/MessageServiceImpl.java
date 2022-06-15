package ru.neoflex.credit.deal.service.impl;

import lombok.RequiredArgsConstructor;
import ru.neoflex.credit.deal.model.Application;
import ru.neoflex.credit.deal.repository.ApplicationRepository;
import ru.neoflex.credit.deal.service.abstracts.MessageService;

import javax.persistence.EntityExistsException;

import static ru.neoflex.credit.deal.model.ApplicationStatusEnum.CC_APPROVED;

@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final ApplicationRepository applicationRepository;
    @Override
    public void send(Long applicationId) {
        Application application = applicationRepository.getReferenceById(applicationId);
        if (application == null) {
            throw new EntityExistsException(String.format("Application with applicationID = %d not exists", applicationId));
        }
        if (application.status() != CC_APPROVED) {
            throw
        }
    }

    @Override
    public void sign(Long applicationId) {

    }

    @Override
    public void code(Long applicationId) {

    }
}
