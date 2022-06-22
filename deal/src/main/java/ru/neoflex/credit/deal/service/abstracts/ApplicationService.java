package ru.neoflex.credit.deal.service.abstracts;

import ru.neoflex.credit.deal.model.ApplicationDTO;

public interface ApplicationService {
    ApplicationDTO getApplicationById(Long applicationId);
    ApplicationDTO updateApplicationStatusById(Long applicationId);
}
