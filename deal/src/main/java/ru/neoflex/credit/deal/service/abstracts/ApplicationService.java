package ru.neoflex.credit.deal.service.abstracts;

import ru.neoflex.credit.deal.model.ApplicationDTO;

import java.util.List;

public interface ApplicationService {
    ApplicationDTO getApplicationById(Long applicationId);
    ApplicationDTO updateApplicationStatusById(Long applicationId, String applicationStatusNew);

    List<ApplicationDTO> getAllApplication();
}
