package ru.neoflex.credit.deal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                topic = "conveyor-finish-registration";
                break;
            case DOCUMENTS_CREATED:
                topic = "conveyor-create-documents";
                break;
            case CREDIT_ISSUE:
                topic = "conveyor-credit-issued";
                break;
            case COMPLETE_DOCUMENT:
                topic = "conveyor-application-denied";
                break;
            case LINK_SIGN:
                topic = "conveyor-send-ses";
                break;
        }
        log.info("topic = {}", topic);
        return topic;
    }
}
