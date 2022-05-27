package ru.neoflex.credit.conveyor.service.impl;

import lombok.RequiredArgsConstructor;
import ru.neoflex.credit.conveyor.model.CreditDTO;
import ru.neoflex.credit.conveyor.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.conveyor.model.LoanOfferDTO;
import ru.neoflex.credit.conveyor.model.ScoringDataDTO;
import ru.neoflex.credit.conveyor.service.abstracts.FacadeService;
import ru.neoflex.credit.conveyor.service.abstracts.PreScoringService;
import ru.neoflex.credit.conveyor.service.abstracts.ScoringService;

import java.util.List;
@RequiredArgsConstructor
public class FacadeServiceImpl implements FacadeService {
    private final PreScoringService preScoringService;
    private final ScoringService scoringService;
    @Override
    public List<LoanOfferDTO> createListOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return preScoringService.createListOffers(loanApplicationRequestDTO);
    }

    @Override
    public CreditDTO scoring(ScoringDataDTO scoringDataDTO) {
        return scoringService.scoring(scoringDataDTO);
    }
}
