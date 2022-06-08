package ru.neoflex.credit.application.service.abstracts;

import org.springframework.http.ResponseEntity;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;

import java.util.List;

public interface ApplicationService {
    void offer(LoanOfferDTO loanOfferDTO);
    List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO request);
}
