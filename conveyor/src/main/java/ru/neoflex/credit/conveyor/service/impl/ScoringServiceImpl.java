package ru.neoflex.credit.conveyor.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.conveyor.model.CreditDTO;
import ru.neoflex.credit.conveyor.model.ScoringDataDTO;
import ru.neoflex.credit.conveyor.service.abstracts.LoanCalculatorService;
import ru.neoflex.credit.conveyor.service.abstracts.ScoringService;

@Service
@RequiredArgsConstructor
public class ScoringServiceImpl implements ScoringService {
    private final LoanCalculatorService calculator;
    @Override
    public CreditDTO scoring(ScoringDataDTO scoringDataDTO) {
        return calculator.calculateCredit(scoringDataDTO);
    }
}
