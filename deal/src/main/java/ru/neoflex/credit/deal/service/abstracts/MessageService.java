package ru.neoflex.credit.deal.service.abstracts;


public interface MessageService {
    void send(Long applicationId);
    void sign(Long applicationId);
    void code(Long applicationId, Integer ses);

    void createDocumentsRequest(Long applicationId);
}
