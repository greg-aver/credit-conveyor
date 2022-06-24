package ru.neoflex.credit.dossier.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.neoflex.credit.deal.model.ApplicationDTO;
import ru.neoflex.credit.deal.model.EmailMessage;
import ru.neoflex.credit.deal.model.MessageKafka;
import ru.neoflex.credit.dossier.feign.DealFeignClient;
import ru.neoflex.credit.dossier.sender.abstracts.SenderEmailService;
import ru.neoflex.credit.dossier.service.abstracts.DocumentService;
import ru.neoflex.credit.dossier.service.abstracts.MessageService;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.neoflex.credit.deal.model.ApplicationStatus.DOCUMENT_CREATED;

@RequiredArgsConstructor
@Component
@Slf4j
public class KafkaConsumer {

    private final MessageService messageService;
    private final DocumentService documentService;
    private final SenderEmailService senderEmailService;
    private final DealFeignClient dealFeignClient;

    @KafkaListener(topics = "${topic.finish-registration}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeFinishRegistration(String messageJson) {
        String stage = String.format("Finish registration. Message = %s", messageJson);
        senderEmailService.sendMessage(
                messageService.convertJsonToEmailMessage(messageJson, stage)
        );
    }

    @KafkaListener(topics = "${topic.create-documents}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeCreateDocuments(String messageJson) {
        String stage = String.format("Create documents. Message = = %s", messageJson);
        senderEmailService.sendMessage(
                messageService.convertJsonToEmailMessage(messageJson, stage)
        );
    }

    @KafkaListener(topics = "${topic.credit-issued}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeCreditIssued(String messageJson) {
        String stage = String.format("Credit issued. Message = = %s", messageJson);
        senderEmailService.sendMessage(
                messageService.convertJsonToEmailMessage(messageJson, stage)
        );
    }

    @KafkaListener(topics = "${topic.application-denied}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeApplicationDenied(String messageJson) {
        String stage = String.format("Application denied. Message = = %s", messageJson);
        senderEmailService.sendMessage(
                messageService.convertJsonToEmailMessage(messageJson, stage)
        );
    }

    @KafkaListener(topics = "${topic.send-ses}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSendSes(String messageJson) {
        String stage = String.format("Send ses. Message = = %s", messageJson);
        senderEmailService.sendMessage(
                messageService.convertJsonToEmailMessage(messageJson, stage)
        );
    }

    @KafkaListener(topics = "${topic.send-documents}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSendDocuments(String messageJson) {
        String stage = String.format("Send documents. Message = = %s", messageJson);
        log.info(stage);
        MessageKafka messageKafka = messageService.getMessageFromJson(messageJson);
        EmailMessage emailMessage = messageService.kafkaMessageToEmailMessage(messageKafka);
        log.info("Email message: {}", emailMessage);

        List<File> files = documentService.createAllDocuments(messageKafka.getApplicationId());
        Map<String, File> attachment = files.stream().collect(Collectors.toMap(File::getName, file -> file));
        log.info("attachment: \n{}", attachment);
        ApplicationDTO application = dealFeignClient.updateApplicationStatusById(messageKafka.getApplicationId(), DOCUMENT_CREATED);
        log.info("current application = {}", application);

        senderEmailService.sendMessageWithAttachment(
                emailMessage.getAddress(), emailMessage.getSubject(), emailMessage.getText(), attachment
        );
    }
}
