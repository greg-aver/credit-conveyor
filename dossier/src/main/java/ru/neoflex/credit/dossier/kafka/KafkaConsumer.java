package ru.neoflex.credit.dossier.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.neoflex.credit.deal.model.EmailMessage;
import ru.neoflex.credit.deal.model.MessageKafka;
import ru.neoflex.credit.dossier.sender.abstracts.SenderEmailService;
import ru.neoflex.credit.dossier.service.abstracts.MessageService;

@RequiredArgsConstructor
@Component
@Slf4j
public class KafkaConsumer {
/*
    @Value("${topic.finish-registration}")
    private final String FINISH_REGISTRATION_TOPIC;
    @Value("${topic.create-documents}")
    private final String CREATE_DOCUMENTS_TOPIC;
    @Value("${topic.credit-issued}")
    private final String CREDIT_ISSUED_TOPIC;
    @Value("${topic.application-denied}")
    private final String APPLICATION_DENIED_TOPIC;
    @Value("${topic.send-ses}")
    private final String SEND_SES_TOPIC;
    @Value("${topic.send-documents}")
    private final String SEND_DOCUMENTS_TOPIC;*/

    private final MessageService messageService;
    private final SenderEmailService senderEmailService;
    @KafkaListener(topics = "${topic.finish-registration}")
    public void consumeFinishRegistration(String messageJson) {
        log.info("Finish registration. Message = {}", messageJson);
        MessageKafka messageKafka = messageService.getMessageFromJson(messageJson);
        EmailMessage emailMessage = messageService.kafkaMessageToEmailMessage(messageKafka);
        log.info("Email message: {}", emailMessage);
        senderEmailService.sendMessage(emailMessage);
    }
}
