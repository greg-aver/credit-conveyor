package ru.neoflex.credit.deal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.credit.deal.api.DealApi;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;
import ru.neoflex.credit.deal.model.ScoringDataDTO;
import ru.neoflex.credit.deal.service.abstracts.DealService;

import java.util.List;
@RestController
@RequiredArgsConstructor
public class DealController implements DealApi {
    private final DealService service;
    @Override
    public ResponseEntity<Void> calculateCredit(Long applicationId, ScoringDataDTO scoringDataDTO) {
        service.calculateCredit(applicationId, scoringDataDTO);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<LoanOfferDTO>> createApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(service.createApplication(loanApplicationRequestDTO));
    }

    @Override
    public ResponseEntity<Void> offer(LoanOfferDTO loanOfferDTO) {
        service.offer(loanOfferDTO);
        return ResponseEntity.ok().build();
    }
}
