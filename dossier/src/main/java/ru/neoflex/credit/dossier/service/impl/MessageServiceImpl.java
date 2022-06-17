package ru.neoflex.credit.dossier.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.deal.model.EmailMessage;
import ru.neoflex.credit.deal.model.MessageKafka;
import ru.neoflex.credit.dossier.feign.DealFeignClient;
import ru.neoflex.credit.dossier.sender.abstracts.SenderEmailService;
import ru.neoflex.credit.dossier.service.abstracts.MessageService;
@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final SenderEmailService senderEmailService;
    private final DealFeignClient dealFeignClient;

    @Override
    public void sendMessage(MessageKafka messageKafka) {
        senderEmailService.sendMessage(kafkaMessageToEmailMessage(messageKafka));
    }

    private EmailMessage kafkaMessageToEmailMessage(MessageKafka messageKafka) {
        String text;
        String subject;

        switch(messageKafka.getTheme()) {
            case SEND_SES:

                break;
            case CREDIT_ISSUED:

                break;
            case SEND_DOCUMENTS:

                break;
            case CREATE_DOCUMENTS:

                break;
            case APPLICATION_DENIED:

                break;
            case FINISH_REGISTRATION:

                break;
        }
        return null;
    }
}
