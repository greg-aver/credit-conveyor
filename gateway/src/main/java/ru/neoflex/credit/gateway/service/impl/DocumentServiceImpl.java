package ru.neoflex.credit.gateway.service.impl;

import org.springframework.stereotype.Service;
import ru.neoflex.credit.gateway.service.abstracts.DocumentService;

@Service
public class DocumentServiceImpl implements DocumentService {
    @Override
    public void sendSesCode(Long applicationId, Integer sesCode) {

    }

    @Override
    public void createDocuments(Long applicationId) {

    }

    @Override
    public void signDocuments(Long applicationId) {

    }
}
