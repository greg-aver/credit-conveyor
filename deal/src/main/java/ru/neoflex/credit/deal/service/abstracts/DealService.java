package ru.neoflex.credit.deal.service.abstracts;

import ru.neoflex.credit.deal.model.FinishRegistrationRequestDTO;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;
import ru.neoflex.credit.deal.model.ScoringDataDTO;

import java.util.List;

public interface DealService {
    List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO loanApplicationRequestDTO);
    void offer(LoanOfferDTO loanOfferDTO);
    void calculateCredit(Long applicationId, FinishRegistrationRequestDTO requestDTO);
}
