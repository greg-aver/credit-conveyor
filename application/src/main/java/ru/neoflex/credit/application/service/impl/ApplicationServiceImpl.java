package ru.neoflex.credit.application.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.application.feign.DealFeignClient;
import ru.neoflex.credit.application.service.abstracts.ApplicationService;
import ru.neoflex.credit.application.validator.ApplicationValidator;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {
    private final DealFeignClient dealFeignClient;
    private final ApplicationValidator validator;

    @Override
    public void offer(LoanOfferDTO loanOfferDTO) {
        dealFeignClient.offer(loanOfferDTO);
    }

    @Override
    public List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO request) {
        validator.preScoring(request);
        List<LoanOfferDTO> loanOffersList = dealFeignClient.createApplication(request).getBody();
        log.info("loanOffersList = {}", loanOffersList);
        return loanOffersList;
    }
}
