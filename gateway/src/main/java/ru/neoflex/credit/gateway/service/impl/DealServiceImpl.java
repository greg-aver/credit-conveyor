package ru.neoflex.credit.gateway.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.deal.model.FinishRegistrationRequestDTO;
import ru.neoflex.credit.deal.model.ScoringDataDTO;
import ru.neoflex.credit.gateway.feign.DealFeignClient;
import ru.neoflex.credit.gateway.service.abstracts.DealService;
@Service
@Slf4j
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {
    private final DealFeignClient dealFeignClient;

    public void finishRegistration(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO) {
        log.info("Start finish registration");
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO()
                .account(finishRegistrationRequestDTO.getAccount())
                .dependentAmount(finishRegistrationRequestDTO.getDependentAmount())
                .employment(finishRegistrationRequestDTO.getEmployment())
                .gender(ScoringDataDTO.GenderEnum.valueOf(finishRegistrationRequestDTO.getGender().name()))
                .maritalStatus(ScoringDataDTO.MaritalStatusEnum.valueOf(finishRegistrationRequestDTO.getMaritalStatus().name()))
                .passportIssueBranch(finishRegistrationRequestDTO.getPassportIssueBranch())
                .passportIssueDate(finishRegistrationRequestDTO.getPassportIssueDate());
        log.info("applicationId = {}, scoringDataDTO = {}", applicationId, scoringDataDTO);
        dealFeignClient.calculateCredit(applicationId, scoringDataDTO);
        log.info("finish registration");
    }
}
