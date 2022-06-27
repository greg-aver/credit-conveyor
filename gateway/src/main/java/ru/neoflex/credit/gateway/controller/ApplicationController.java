package ru.neoflex.credit.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.credit.deal.api.ApplicationApi;
import ru.neoflex.credit.deal.model.FinishRegistrationRequestDTO;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;
import ru.neoflex.credit.gateway.service.abstracts.ApplicationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationController implements ApplicationApi {
    private final ApplicationService applicationService;
    @Override
    public ResponseEntity<Void> applyOffer(LoanOfferDTO loanOfferDTO) {
        applicationService.applyOffer(loanOfferDTO);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<LoanOfferDTO>> createLoanApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(applicationService.createLoanApplication(loanApplicationRequestDTO));
    }

    @Override
    public ResponseEntity<Void> denyLoanApplication(Long applicationId) {
        applicationService.denyLoanApplication(applicationId);
        return  ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> finishRegistration(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO) {
        applicationService.finishRegistration(applicationId, finishRegistrationRequestDTO);
        return  ResponseEntity.ok().build();
    }
}
