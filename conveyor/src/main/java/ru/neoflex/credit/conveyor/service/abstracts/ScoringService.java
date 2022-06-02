package ru.neoflex.credit.conveyor.service.abstracts;

import ru.neoflex.credit.conveyor.model.CreditDTO;
import ru.neoflex.credit.conveyor.model.ScoringDataDTO;

public interface ScoringService {
    CreditDTO scoring(ScoringDataDTO scoringDataDTO);
}
