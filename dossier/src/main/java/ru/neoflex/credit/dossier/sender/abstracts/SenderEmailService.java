package ru.neoflex.credit.dossier.sender.abstracts;

import ru.neoflex.credit.deal.model.EmailMessage;

import java.io.File;
import java.util.Map;

public interface SenderEmailService {
    void sendMessage(String toAddress, String subject, String message);
    void sendMessage(EmailMessage emailMessage);

    void sendMessageWithAttachment(String toAddress, String subject, String text, Map<String, File> attachmentFiles);
}
