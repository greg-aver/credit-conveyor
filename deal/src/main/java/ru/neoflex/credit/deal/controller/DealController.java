package ru.neoflex.credit.deal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.credit.deal.api.DealApi;
import ru.neoflex.credit.deal.model.*;
import ru.neoflex.credit.deal.service.abstracts.ApplicationService;
import ru.neoflex.credit.deal.service.abstracts.DealService;
import ru.neoflex.credit.deal.service.abstracts.MessageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DealController implements DealApi {
    private final DealService service;
    private final ApplicationService applicationService;
    private final MessageService messageService;

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

    @Override
    public ResponseEntity<List<ApplicationDTO>> getAllApplication() {
        return ResponseEntity.ok(applicationService.getAllApplication());
    }

    @Override
    public ResponseEntity<ApplicationDTO> getApplicationById(Long applicationId) {
        return ResponseEntity.ok(applicationService.getApplicationById(applicationId));
    }

    @Override
    public ResponseEntity<ApplicationDTO> updateApplicationStatusById(Long applicationId, String applicationStatus) {
        return ResponseEntity.ok(applicationService.updateApplicationStatusById(applicationId, applicationStatus));
    }


    @Override
    public ResponseEntity<Void> code(Long applicationId, Integer ses) {
        messageService.code(applicationId, ses);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> send(Long applicationId) {
        messageService.send(applicationId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> sign(Long applicationId) {
        messageService.sign(applicationId);
        return ResponseEntity.ok().build();
    }
}
