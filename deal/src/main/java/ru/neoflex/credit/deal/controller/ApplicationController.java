package ru.neoflex.credit.deal.controller;

import org.springframework.http.ResponseEntity;
import ru.neoflex.credit.deal.api.DealApi;
import ru.neoflex.credit.deal.model.ApplicationDTO;

public class ApplicationController implements DealApi {
    @Override
    public ResponseEntity<ApplicationDTO> getApplicationById(Long applicationId) {
        return DealApi.super.getApplicationById(applicationId);
    }

    @Override
    public ResponseEntity<ApplicationDTO> updateApplicationStatusById(Long applicationId) {
        return DealApi.super.updateApplicationStatusById(applicationId);
    }
}
