package ru.neoflex.credit.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.credit.deal.api.DocumentApi;
import ru.neoflex.credit.gateway.service.abstracts.DocumentService;

@RestController
@RequiredArgsConstructor
public class DocumentController implements DocumentApi {
    private final DocumentService documentService;
    @Override
    public ResponseEntity<Void> createDocuments(Long applicationId) {
        documentService.createDocuments(applicationId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> sendSesCode(Long applicationId, Integer sesCode) {
        documentService.sendSesCode(applicationId, sesCode);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> signDocuments(Long applicationId) {
        documentService.signDocuments(applicationId);
        return ResponseEntity.ok().build();
    }
}
