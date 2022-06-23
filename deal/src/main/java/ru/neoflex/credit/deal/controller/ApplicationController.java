package ru.neoflex.credit.deal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.credit.deal.api.DealApi;
import ru.neoflex.credit.deal.model.ApplicationDTO;
import ru.neoflex.credit.deal.service.abstracts.ApplicationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationController implements DealApi {
    private final ApplicationService applicationService;

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
}
