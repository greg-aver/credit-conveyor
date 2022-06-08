package ru.neoflex.credit.application.service.impl;

import ru.neoflex.credit.application.service.abstracts.ApplicationService;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;

import java.util.List;

public class ApplicationServiceImpl implements ApplicationService {
    @Override
    public void offer(LoanOfferDTO loanOfferDTO) {

    }

    @Override
    public List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO request) {
        return null;
    }
}
