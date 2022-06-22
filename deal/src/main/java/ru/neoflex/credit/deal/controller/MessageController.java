package ru.neoflex.credit.deal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.credit.deal.api.DealApi;
import ru.neoflex.credit.deal.service.abstracts.MessageService;
@RequiredArgsConstructor
@RestController
public class MessageController implements DealApi {
    private final MessageService messageService;

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
