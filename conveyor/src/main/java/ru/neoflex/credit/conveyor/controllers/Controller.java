package ru.neoflex.credit.conveyor.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.credit.conveyor.api.ConveyorApi;
import ru.neoflex.credit.conveyor.model.CreditDTO;
import ru.neoflex.credit.conveyor.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.conveyor.model.LoanOfferDTO;
import ru.neoflex.credit.conveyor.model.ScoringDataDTO;
import ru.neoflex.credit.conveyor.service.abstracts.PreScoringService;
import ru.neoflex.credit.conveyor.service.abstracts.ScoringService;

import java.util.List;

@RestController
public class Controller implements ConveyorApi {

    private final PreScoringService preScoringService;
    private final ScoringService scoringService;

    @Autowired
    public Controller(PreScoringService preScoringService, ScoringService scoringService) {
        this.preScoringService = preScoringService;
        this.scoringService = scoringService;
    }

    @Override
    public ResponseEntity<List<LoanOfferDTO>> createOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return new ResponseEntity<>(preScoringService.createListOffers(loanApplicationRequestDTO), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CreditDTO> scoring(ScoringDataDTO scoringDataDTO) {
        return new ResponseEntity<>(scoringService.scoring(scoringDataDTO), HttpStatus.OK);
    }
}
