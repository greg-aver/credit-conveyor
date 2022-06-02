package ru.neoflex.credit.deal.service.impl;

import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;
import ru.neoflex.credit.deal.model.ScoringDataDTO;
import ru.neoflex.credit.deal.service.abstracts.DealService;

import java.util.List;

public class DealServiceImpl  implements DealService {
    @Override
    public List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return null;
    }

    @Override
    public void offer(LoanOfferDTO loanOfferDTO) {

    }

    @Override
    public void calculateCredit(Long applicationId, ScoringDataDTO scoringDataDTO) {

    }
}
