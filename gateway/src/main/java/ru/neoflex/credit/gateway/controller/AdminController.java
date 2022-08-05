package ru.neoflex.credit.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.credit.deal.api.AdminApi;
import ru.neoflex.credit.deal.model.ApplicationDTO;
import ru.neoflex.credit.gateway.service.abstracts.AdminService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController implements AdminApi {
    private final AdminService adminService;
    @Override
    public ResponseEntity<List<ApplicationDTO>> getAllApplications() {
        return ResponseEntity.ok(adminService.getAllApplications());
    }

    @Override
    public ResponseEntity<ApplicationDTO> getApplicationById(Long applicationId) {
        return ResponseEntity.ok(adminService.getApplicationById(applicationId));
    }
}
