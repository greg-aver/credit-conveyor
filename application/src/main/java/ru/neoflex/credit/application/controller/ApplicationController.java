package ru.neoflex.credit.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.credit.application.service.abstracts.ApplicationService;
import ru.neoflex.credit.deal.api.ApplicationApi;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ApplicationController implements ApplicationApi {
    private final ApplicationService service;

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
