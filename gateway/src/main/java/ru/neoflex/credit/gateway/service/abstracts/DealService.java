package ru.neoflex.credit.gateway.service.abstracts;

import ru.neoflex.credit.deal.model.FinishRegistrationRequestDTO;

public interface DealService {
    void finishRegistration(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO);
}
