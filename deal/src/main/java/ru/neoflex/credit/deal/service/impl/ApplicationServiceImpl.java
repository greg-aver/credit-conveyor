package ru.neoflex.credit.deal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.deal.exception.ApplicationNotFoundException;
import ru.neoflex.credit.deal.mapper.ApplicationMapper;
import ru.neoflex.credit.deal.model.Application;
import ru.neoflex.credit.deal.model.ApplicationDTO;
import ru.neoflex.credit.deal.repository.ApplicationRepository;
import ru.neoflex.credit.deal.service.abstracts.ApplicationService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationMapper applicationMapper;
    private final ApplicationRepository applicationRepository;
    @Override
    public ApplicationDTO getApplicationById(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        String.format("Application with id %d not found", applicationId)
                ));
        log.info("Application with id = {}: \n {}",applicationId, application);
        ApplicationDTO applicationDTO = applicationMapper.toDto(application);
        log.info("Application with id = {}: \n {}", applicationId, applicationDTO);
        return applicationDTO;
    }

    @Override
    public ApplicationDTO updateApplicationStatusById(Long applicationId) {
        return null;
    }
}
