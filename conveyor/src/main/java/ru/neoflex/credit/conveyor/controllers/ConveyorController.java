package ru.neoflex.credit.conveyor.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.credit.conveyor.ConveyorApi;
import ru.neoflex.credit.conveyor.model.CreditDTO;
import ru.neoflex.credit.conveyor.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.conveyor.model.LoanOfferDTO;
import ru.neoflex.credit.conveyor.model.ScoringDataDTO;
import ru.neoflex.credit.conveyor.service.abstracts.ScoringFacadeService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ConveyorController implements ConveyorApi {

    private final ScoringFacadeService scoringFacadeService;

    @Override
    public ResponseEntity<List<LoanOfferDTO>> createOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(scoringFacadeService.createListOffers(loanApplicationRequestDTO));
    }

    @Override
    public ResponseEntity<CreditDTO> scoring(ScoringDataDTO scoringDataDTO) {
        return ResponseEntity.ok(scoringFacadeService.scoring(scoringDataDTO));
    }
}
