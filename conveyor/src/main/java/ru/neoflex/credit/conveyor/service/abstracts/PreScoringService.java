package ru.neoflex.credit.conveyor.service.abstracts;

import ru.neoflex.credit.conveyor.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.conveyor.model.LoanOfferDTO;

import java.util.List;

public interface PreScoringService {
    List<LoanOfferDTO> createListOffers(LoanApplicationRequestDTO loanApplicationRequestDTO);
}
