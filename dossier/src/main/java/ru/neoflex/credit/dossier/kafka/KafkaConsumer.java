package ru.neoflex.credit.dossier.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.neoflex.credit.deal.model.EmailMessage;
import ru.neoflex.credit.dossier.sender.abstracts.SenderEmailService;
import ru.neoflex.credit.dossier.service.abstracts.MessageService;

import java.io.File;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class KafkaConsumer {

    private final MessageService messageService;
    private final SenderEmailService senderEmailService;

    @KafkaListener(topics = "${topic.finish-registration}")
    public void consumeFinishRegistration(String messageJson) {
        String stage = String.format("Finish registration. Message = %s", messageJson);
        senderEmailService.sendMessage(
                messageService.convertJsonToEmailMessage(messageJson, stage)
        );
    }

    @KafkaListener(topics = "${topic.create-documents}")
    public void consumeCreateDocuments(String messageJson) {
        String stage = String.format("Create documents. Message = = %s", messageJson);
        senderEmailService.sendMessage(
                messageService.convertJsonToEmailMessage(messageJson, stage)
        );
    }

    @KafkaListener(topics = "${topic.credit-issued}")
    public void consumeCreditIssued(String messageJson) {
        String stage = String.format("Credit issued. Message = = %s", messageJson);
        senderEmailService.sendMessage(
                messageService.convertJsonToEmailMessage(messageJson, stage)
        );
    }

    @KafkaListener(topics = "${topic.application-denied}")
    public void consumeApplicationDenied(String messageJson) {
        String stage = String.format("Application denied. Message = = %s", messageJson);
        senderEmailService.sendMessage(
                messageService.convertJsonToEmailMessage(messageJson, stage)
        );
    }

    @KafkaListener(topics = "${topic.send-ses}")
    public void consumeSendSes(String messageJson) {
        String stage = String.format("Send ses. Message = = %s", messageJson);
        senderEmailService.sendMessage(
                messageService.convertJsonToEmailMessage(messageJson, stage)
        );
    }

    @KafkaListener(topics = "${topic.send-documents}")
    public void consumeSendDocuments(String messageJson) {
        String stage = String.format("Send documents. Message = = %s", messageJson);
        EmailMessage emailMessage = messageService.convertJsonToEmailMessage(messageJson, stage);

        //TODO: Создать спецальный сервис для работы с документами. В нем метод createDocuments

        Map<String, File> attachment = null;

        senderEmailService.sendMessageWithAttachment(
                emailMessage.getAddress(), emailMessage.getSubject(), emailMessage.getText(), attachment
        );
    }
}
