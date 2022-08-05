package ru.neoflex.credit.gateway.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.deal.model.ApplicationDTO;
import ru.neoflex.credit.gateway.feign.DealFeignClient;
import ru.neoflex.credit.gateway.service.abstracts.AdminService;

import java.util.List;
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final DealFeignClient dealFeignClient;

    public ApplicationDTO getApplicationById(Long applicationId) {
        return dealFeignClient.getApplicationById(applicationId);
    }

    public List<ApplicationDTO> getAllApplications() {
        return dealFeignClient.getAllApplications();
    }
}
