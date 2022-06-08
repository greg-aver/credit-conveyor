package ru.neoflex.credit.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.application.feign.DealFeignClient;
import ru.neoflex.credit.application.service.abstracts.ApplicationService;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {
    private final DealFeignClient dealFeignClient;
    @Override
    public void offer(LoanOfferDTO loanOfferDTO) {
        dealFeignClient.offer(loanOfferDTO);
    }

    private void preScoring(LoanApplicationRequestDTO request) {

    }
    @Override
    public List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO request) {
        preScoring(request);
        return null;
    }
}
