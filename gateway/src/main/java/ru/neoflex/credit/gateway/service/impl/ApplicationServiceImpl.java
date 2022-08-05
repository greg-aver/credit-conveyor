package ru.neoflex.credit.gateway.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.deal.model.ApplicationStatus;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;
import ru.neoflex.credit.gateway.feign.ApplicationFeignClient;
import ru.neoflex.credit.gateway.feign.DealFeignClient;
import ru.neoflex.credit.gateway.service.abstracts.ApplicationService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationFeignClient applicationFeignClient;

    private final DealFeignClient dealFeignClient;

    public List<LoanOfferDTO> createLoanApplication(LoanApplicationRequestDTO request) {
        log.info("LoanApplicationRequestDTO = {}", request);
        return applicationFeignClient.createApplication(request).getBody();
    }

    public void applyOffer(LoanOfferDTO loanOfferDTO) {
        log.info("loanOfferDTO = {}", loanOfferDTO);
        applicationFeignClient.applyOffer(loanOfferDTO);
    }

    public void denyLoanApplication(Long applicationId) {
        dealFeignClient.updateApplicationStatusById(applicationId, ApplicationStatus.CLIENT_DENIED.name());
    }
}
