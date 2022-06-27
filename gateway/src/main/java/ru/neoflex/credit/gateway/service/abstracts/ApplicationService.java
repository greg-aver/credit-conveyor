package ru.neoflex.credit.gateway.service.abstracts;

import ru.neoflex.credit.deal.model.FinishRegistrationRequestDTO;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;

import java.util.List;

public interface ApplicationService {
    void denyLoanApplication(Long applicationId);

    void applyOffer(LoanOfferDTO loanOfferDTO);

    List<LoanOfferDTO> createLoanApplication(LoanApplicationRequestDTO loanApplicationRequestDTO);

    void finishRegistration(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO);
}
