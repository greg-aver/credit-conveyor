package ru.neoflex.credit.gateway.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.gateway.feign.DealFeignClient;
import ru.neoflex.credit.gateway.service.abstracts.DocumentService;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DealFeignClient dealFeignClient;

    public void createDocuments(Long applicationId) {
        dealFeignClient.send(applicationId);
    }

    public void sendSesCode(Long applicationId, Integer sesCode) {
        dealFeignClient.code(applicationId, sesCode);
    }

    public void signDocuments(Long applicationId) {
        dealFeignClient.sign(applicationId);
    }
}
