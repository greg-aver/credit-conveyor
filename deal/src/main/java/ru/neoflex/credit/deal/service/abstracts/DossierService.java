package ru.neoflex.credit.deal.service.abstracts;

import ru.neoflex.credit.deal.model.EmailMessage;

public interface DossierService {
    void send(EmailMessage message);
}
