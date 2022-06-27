package ru.neoflex.credit.gateway.service.abstracts;

public interface DocumentService {
    void sendSesCode(Long applicationId, Integer sesCode);

    void createDocuments(Long applicationId);

    void signDocuments(Long applicationId);
}
