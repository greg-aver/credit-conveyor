package ru.neoflex.credit.dossier.service.abstracts;

import ru.neoflex.credit.deal.model.EmailMessage;
import ru.neoflex.credit.deal.model.MessageKafka;

public interface MessageService {
    void sendMessage(MessageKafka messageKafka);
    MessageKafka getMessageFromJson(String messageJson);
    EmailMessage kafkaMessageToEmailMessage(MessageKafka messageKafka);
}
