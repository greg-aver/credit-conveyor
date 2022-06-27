package ru.neoflex.credit.gateway.service.impl;

import org.springframework.stereotype.Service;
import ru.neoflex.credit.deal.model.FinishRegistrationRequestDTO;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;
import ru.neoflex.credit.gateway.service.abstracts.ApplicationService;

import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    @Override
    public void denyLoanApplication(Long applicationId) {

    }

    @Override
    public void applyOffer(LoanOfferDTO loanOfferDTO) {

    }

    @Override
    public List<LoanOfferDTO> createLoanApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return null;
    }

    @Override
    public void finishRegistration(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO) {

    }
}
