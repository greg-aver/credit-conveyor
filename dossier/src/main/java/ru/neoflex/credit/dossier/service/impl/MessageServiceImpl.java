package ru.neoflex.credit.dossier.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.deal.model.EmailMessage;
import ru.neoflex.credit.deal.model.MessageKafka;
import ru.neoflex.credit.dossier.exception.IncorrectMessageThemeException;
import ru.neoflex.credit.dossier.feign.DealFeignClient;
import ru.neoflex.credit.dossier.sender.abstracts.SenderEmailService;
import ru.neoflex.credit.dossier.service.abstracts.MessageService;
@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final SenderEmailService senderEmailService;
    private final DealFeignClient dealFeignClient;

    @Value("${email-message.send-document.subject}")
    private final String SEND_DOCUMENT_SUBJECT;
    @Value("${email-message.send-document.text}")
    private final String SEND_DOCUMENT_TEXT;

    @Value("${email-message.send-ses.subject}")
    private final String SEND_SES_SUBJECT;
    @Value("${email-message.send-ses.text}")
    private final String SEND_SES_TEXT;

    @Value("${email-message.create-document.subject}")
    private final String CREATE_DOCUMENT_SUBJECT;
    @Value("${email-message.create-document.text}")
    private final String CREATE_DOCUMENT_TEXT;

    @Value("${email-message.credit-issued.subject}")
    private final String CREDIT_ISSUED_SUBJECT;
    @Value("${email-message.credit-issued.text}")
    private final String CREDIT_ISSUED_TEXT;

    @Value("${email-message.finish-registration.subject}")
    private final String FINISH_REGISTRATION_SUBJECT;
    @Value("${email-message.finish-registration.text}")
    private final String FINISH_REGISTRATION_TEXT;

    @Value("${email-message.application-denied.subject}")
    private final String APPLICATION_DENIED_SUBJECT;
    @Value("${email-message.application-denied.text}")
    private final String APPLICATION_DENIED_TEXT;

    private final ObjectMapper objectMapper;

    @Override
    public MessageKafka getMessageFromJson(String messageJson) {
        log.info("Start attempting conversion message json {} to MessageKafka", messageJson);
        try {
            MessageKafka messageKafka = objectMapper.readValue(messageJson, MessageKafka.class);
            log.debug("Attempt was successful. Result: {}", messageKafka);
            return messageKafka;
        } catch (JsonProcessingException e) {
            log.error("Failed to get messageKafka {} from JSON", messageJson);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(MessageKafka messageKafka) {
        senderEmailService.sendMessage(kafkaMessageToEmailMessage(messageKafka));
    }

    @Override
    public EmailMessage kafkaMessageToEmailMessage(MessageKafka messageKafka) {
        String text = null;
        String subject = null;
        Long applicationId = dealFeignClient
                .getApplicationById(messageKafka.getApplicationId())
                .getId();

        switch(messageKafka.getTheme()) {
            case SEND_SES:
                subject = SEND_SES_SUBJECT;
                text = SEND_SES_TEXT.replace("{applicationId}", applicationId.toString());
                break;
            case CREDIT_ISSUED:
                subject = CREDIT_ISSUED_SUBJECT;
                text = CREDIT_ISSUED_TEXT.replace("{applicationId}", applicationId.toString());
                break;
            case SEND_DOCUMENTS:
                subject = SEND_DOCUMENT_SUBJECT;
                text = SEND_DOCUMENT_TEXT.replace("{applicationId}", applicationId.toString());
                break;
            case CREATE_DOCUMENTS:
                subject = CREATE_DOCUMENT_SUBJECT;
                text = CREATE_DOCUMENT_TEXT.replace("{applicationId}", applicationId.toString());
                break;
            case APPLICATION_DENIED:
                subject = APPLICATION_DENIED_SUBJECT;
                text = APPLICATION_DENIED_TEXT.replace("{applicationId}", applicationId.toString());
                break;
            case FINISH_REGISTRATION:
                subject = FINISH_REGISTRATION_SUBJECT;
                text = FINISH_REGISTRATION_TEXT.replace("{applicationId}", applicationId.toString());
                break;
            default:
                String error = String.format("Incorrect message theme %s", messageKafka.getTheme());
                log.error(error);
                throw new IncorrectMessageThemeException(error);
        }

        EmailMessage emailMessage = new EmailMessage()
                .address(messageKafka.getAddress())
                .text(text)
                .subject(subject);
        log.info("email message = {}", emailMessage);
        return emailMessage;
    }


}
