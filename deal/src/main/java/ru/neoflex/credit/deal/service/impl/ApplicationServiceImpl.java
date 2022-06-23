package ru.neoflex.credit.deal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.deal.exception.ApplicationNotFoundException;
import ru.neoflex.credit.deal.mapper.ApplicationMapper;
import ru.neoflex.credit.deal.model.*;
import ru.neoflex.credit.deal.repository.ApplicationRepository;
import ru.neoflex.credit.deal.service.abstracts.ApplicationService;
import ru.neoflex.credit.deal.service.abstracts.DossierService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.neoflex.credit.deal.model.ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC;
import static ru.neoflex.credit.deal.model.EmailMessage.ThemeEnum.APPLICATION_DENIED;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationMapper applicationMapper;
    private final ApplicationRepository applicationRepository;
    private final DossierService dossierService;

    @Override
    public ApplicationDTO getApplicationById(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        String.format("Application with id %d not found", applicationId)
                ));
        log.info("Application with id = {}: \n {}", applicationId, application);
        ApplicationDTO applicationDTO = applicationMapper.toDto(application);
        log.info("Application with id = {}: \n {}", applicationId, applicationDTO);
        return applicationDTO;
    }

    @Override
    public ApplicationDTO updateApplicationStatusById(Long applicationId, String applicationStatusNew) {
        ApplicationStatusEnum status = ApplicationStatusEnum.valueOf(applicationStatusNew);
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        String.format("Application with id %d not found", applicationId)
                ));
        log.info("Application with id = {}: \n {}", applicationId, application);
        log.info("Update application status. \nApplicationId: {}\nOld application status: {}\nNew application status: {}",
                applicationId, application.status(), applicationStatusNew);
        List<ApplicationStatusHistoryDTO> statusHistory = application.statusHistory();
        statusHistory.add(new ApplicationStatusHistoryDTO()
                .changeType(AUTOMATIC)
                .status(status)
                .time(LocalDateTime.now()));

        if (applicationStatusNew.equals(APPLICATION_DENIED.getValue())) {
            log.info("ApplicationID: {} New status: APPLICATION_DENIED", applicationId);
            dossierService.send(new EmailMessage()
                    .applicationId(applicationId)
                    .theme(APPLICATION_DENIED)
                    .address(application.client().getEmail()));
        }
        application.statusHistory(statusHistory);
        application.status(status);
        Application applicationNew = applicationRepository.save(application);
        log.info("Update application:\n {}", applicationNew);
        return applicationMapper.toDto(applicationNew);
    }

    @Override
    public List<ApplicationDTO> getAllApplication() {
        return applicationRepository.findAll().stream().map(applicationMapper::toDto).collect(Collectors.toList());
    }
}
