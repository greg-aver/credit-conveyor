package ru.neoflex.credit.dossier.sender.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.dossier.sender.abstracts.SenderEmailService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SenderEmailServiceImpl implements SenderEmailService {
    private final JavaMailSender mailSender;
    @Value("${sender.from.email}")
    private final String FROM_EMAIL;

    @Override
    public void sendMessage(String toAddress, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(toAddress);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
        log.info("Send email: {}", message);
    }

    @Override
    public void sendMessageWithAttachment(String toAddress, String subject, String text, Map<String, File> attachmentFiles) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(
                    mimeMessage, true
            );
            messageHelper.setFrom(FROM_EMAIL);
            messageHelper.setTo(toAddress);
            messageHelper.setSubject(subject);
            messageHelper.setText(text);
            for (Map.Entry<String, File> entry : attachmentFiles.entrySet()) {
                messageHelper.addAttachment(entry.getKey(), entry.getValue());
            }
        } catch (MessagingException e) {
            log.error("Having trouble attaching files: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
