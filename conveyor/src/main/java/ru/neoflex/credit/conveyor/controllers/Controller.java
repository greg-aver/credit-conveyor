package ru.neoflex.credit.conveyor.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.credit.conveyor.api.ConveyorApi;
import ru.neoflex.credit.conveyor.model.CreditDTO;
import ru.neoflex.credit.conveyor.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.conveyor.model.LoanOfferDTO;
import ru.neoflex.credit.conveyor.model.ScoringDataDTO;
import ru.neoflex.credit.conveyor.service.abstracts.FacadeService;
import ru.neoflex.credit.conveyor.service.abstracts.PreScoringService;
import ru.neoflex.credit.conveyor.service.abstracts.ScoringService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class Controller implements ConveyorApi {

    private final FacadeService facadeService;

    @Override
    public ResponseEntity<List<LoanOfferDTO>> createOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(facadeService.createListOffers(loanApplicationRequestDTO));
    }

    @Override
    public ResponseEntity<CreditDTO> scoring(ScoringDataDTO scoringDataDTO) {
        return ResponseEntity.ok(facadeService.scoring(scoringDataDTO));
    }
}
