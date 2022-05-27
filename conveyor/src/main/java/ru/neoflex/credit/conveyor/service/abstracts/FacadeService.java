package ru.neoflex.credit.conveyor.service.abstracts;

import ru.neoflex.credit.conveyor.model.CreditDTO;
import ru.neoflex.credit.conveyor.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.conveyor.model.LoanOfferDTO;
import ru.neoflex.credit.conveyor.model.ScoringDataDTO;

import java.util.List;

public interface FacadeService {
    List<LoanOfferDTO> createListOffers(LoanApplicationRequestDTO loanApplicationRequestDTO);
    CreditDTO scoring(ScoringDataDTO scoringDataDTO);
}
