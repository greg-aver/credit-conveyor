package ru.neoflex.credit.dossier.sender.abstracts;

import java.io.File;
import java.util.Map;

public interface SenderEmailService {
    void sendMessage(String toAddress, String subject, String message);

    void sendMessageWithAttachment(String toAddress, String subject, String text, Map<String, File> attachmentFiles);
}
