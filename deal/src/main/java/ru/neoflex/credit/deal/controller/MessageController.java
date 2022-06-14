package ru.neoflex.credit.deal.controller;

import org.springframework.http.ResponseEntity;
import ru.neoflex.credit.deal.api.DealApi;

public class MessageController implements DealApi {
    @Override
    public ResponseEntity<Void> code(Long applicationId) {
        return DealApi.super.code(applicationId);
    }

    @Override
    public ResponseEntity<Void> send(Long applicationId) {
        return DealApi.super.send(applicationId);
    }

    @Override
    public ResponseEntity<Void> sign(Long applicationId) {
        return DealApi.super.sign(applicationId);
    }
}
