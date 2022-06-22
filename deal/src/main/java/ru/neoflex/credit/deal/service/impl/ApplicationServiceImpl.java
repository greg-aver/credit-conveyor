package ru.neoflex.credit.deal.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.deal.model.ApplicationDTO;
import ru.neoflex.credit.deal.service.abstracts.ApplicationService;

@Service
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {
    @Override
    public ApplicationDTO getApplicationById(Long applicationId) {
        return null;
    }

    @Override
    public ApplicationDTO updateApplicationStatusById(Long applicationId) {
        return null;
    }
}
