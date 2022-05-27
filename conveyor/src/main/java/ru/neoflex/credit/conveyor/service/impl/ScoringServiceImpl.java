package ru.neoflex.credit.conveyor.service.impl;

import org.springframework.stereotype.Service;
import ru.neoflex.credit.conveyor.model.CreditDTO;
import ru.neoflex.credit.conveyor.model.ScoringDataDTO;
import ru.neoflex.credit.conveyor.service.abstracts.ScoringService;

@Service
public class ScoringServiceImpl implements ScoringService {
    @Override
    public CreditDTO scoring(ScoringDataDTO scoringDataDTO) {
        return null;
    }
}
