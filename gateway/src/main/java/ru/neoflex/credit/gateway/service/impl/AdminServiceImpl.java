package ru.neoflex.credit.gateway.service.impl;

import org.springframework.stereotype.Service;
import ru.neoflex.credit.deal.model.ApplicationDTO;
import ru.neoflex.credit.gateway.service.abstracts.AdminService;

import java.util.List;
@Service
public class AdminServiceImpl implements AdminService {
    @Override
    public List<ApplicationDTO> getAllApplications() {
        return null;
    }

    @Override
    public ApplicationDTO getApplicationById(Long applicationId) {
        return null;
    }
}
