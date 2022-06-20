package ru.neoflex.credit.dossier.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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

    @KafkaListener(topics = "${topic.finish-registration}")
    public void consumeFinishRegistration(String message) {
        log.debug("Finish registration. Message = {}", message);
    }
}
