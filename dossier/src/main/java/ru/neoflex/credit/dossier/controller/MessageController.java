package ru.neoflex.credit.dossier.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.credit.deal.model.EmailMessage;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final JavaMailSender mailSender;
    @PostMapping("/test")
    public ResponseEntity<Void> senMassage(@RequestBody EmailMessage emailMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("konveierov@yandex.ru");
        message.setTo(emailMessage.getAddress());
        message.setSubject(emailMessage.getSubject());
        message.setText(emailMessage.getText());
        mailSender.send(message);
        log.info("Send email: {}", message);
        return ResponseEntity.ok().build();
    }
}
