package ru.neoflex.credit.gateway.service.abstracts;

import ru.neoflex.credit.deal.model.ApplicationDTO;

import java.util.List;

public interface AdminService {
    List<ApplicationDTO> getAllApplications();
    ApplicationDTO getApplicationById(Long applicationId);
}
