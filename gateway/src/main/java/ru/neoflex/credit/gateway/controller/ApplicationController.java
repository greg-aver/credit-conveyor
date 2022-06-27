package ru.neoflex.credit.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.credit.deal.api.ApplicationApi;
import ru.neoflex.credit.deal.model.FinishRegistrationRequestDTO;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;

import java.util.List;

@RestController
public class ApplicationController implements ApplicationApi {
    @Override
    public ResponseEntity<Void> applyOffer(LoanOfferDTO loanOfferDTO) {
        return ApplicationApi.super.applyOffer(loanOfferDTO);
    }

    @Override
    public ResponseEntity<List<LoanOfferDTO>> createLoanApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ApplicationApi.super.createLoanApplication(loanApplicationRequestDTO);
    }

    @Override
    public ResponseEntity<Void> denyLoanApplication(Long applicationId) {
        return ApplicationApi.super.denyLoanApplication(applicationId);
    }

    @Override
    public ResponseEntity<Void> finishRegistration(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO) {
        return ApplicationApi.super.finishRegistration(applicationId, finishRegistrationRequestDTO);
    }
}
