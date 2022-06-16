package ru.neoflex.credit.deal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.deal.model.EmailMessage;
import ru.neoflex.credit.deal.service.abstracts.DossierService;

import static ru.neoflex.credit.deal.model.EmailMessage.ThemeEnum;

@Service
@RequiredArgsConstructor
@Slf4j
public class DossierServiceImpl implements DossierService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

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


    @Override
    public void send(EmailMessage message) {
        String topic = defineTopic(message.getTheme());
        String messageJson = null;
        try {
            messageJson = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("Conversion error to json. Message = {}", message);
            throw new RuntimeException(e);
        }
        kafkaTemplate.send(topic, messageJson);
        log.info("Send message kafka. MessageJson = {}, topic ={}", messageJson, topic);
    }

    private String defineTopic(ThemeEnum theme) {
        String topic = null;
        switch (theme) {
            case FINISH_REGISTRATION:
                topic = FINISH_REGISTRATION_TOPIC;
                break;
            case CREATE_DOCUMENTS:
                topic = CREATE_DOCUMENTS_TOPIC;
                break;
            case CREDIT_ISSUED:
                topic = CREDIT_ISSUED_TOPIC;
                break;
            case APPLICATION_DENIED:
                topic = APPLICATION_DENIED_TOPIC;
                break;
            case SEND_SES:
                topic = SEND_SES_TOPIC;
                break;
        }
        log.info("topic = {}", topic);
        return topic;
    }
}
