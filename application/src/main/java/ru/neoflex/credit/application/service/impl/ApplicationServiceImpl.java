package ru.neoflex.credit.application.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.application.exception.PreScoringException;
import ru.neoflex.credit.application.feign.DealFeignClient;
import ru.neoflex.credit.application.service.abstracts.ApplicationService;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {
    private final DealFeignClient dealFeignClient;

    @Override
    public void offer(LoanOfferDTO loanOfferDTO) {
        dealFeignClient.offer(loanOfferDTO);
    }

    @Override
    public List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO request) {
        preScoring(request);
        List<LoanOfferDTO> loanOffersList = dealFeignClient.createApplication(request).getBody();
        log.info("loanOffersList = {}", loanOffersList);
        return loanOffersList;
    }

    /* Правила прескоринга (можно придумать новые правила или изменить существующие):

    1. Имя, Фамилия - от 2 до 30 латинских букв. Отчество, при наличии - от 2 до 30 латинских букв.
    2. Сумма кредита - действительно число, большее или равное 10000.
    3. Срок кредита - целое число, большее или равное 6.
    4. Дата рождения - число в формате гггг-мм-дд, не позднее 18 лет с текущего дня.
    5. Email адрес - строка, подходящая под паттерн [\w\.]{2,50}@[\w\.]{2,20}
    6. Серия паспорта - 4 цифры, номер паспорта - 6 цифр.**/
    private void preScoring(LoanApplicationRequestDTO request) {
        log.info("Start pre-scoring");
        ArrayList<String> reasonsRefusal = new ArrayList<>();

        if (!request.getFirstName().matches("[A-Za-z]{2,30}")) {
            reasonsRefusal.add("Incorrect first name by client");
        }

        if (!request.getLastName().matches("[A-Za-z]{2,30}")) {
            reasonsRefusal.add("Incorrect last name by client");
        }

        if (request.getMiddleName() != null &&  request.getMiddleName().matches("[A-Za-z]{2,30}")) {
            reasonsRefusal.add("Incorrect middle name by client");
        }

        if (request.getAmount().compareTo(BigDecimal.valueOf(10000)) < 0) {
            reasonsRefusal.add("Amount < 10_000");
        }

        if (request.getTerm() < 6) {
            reasonsRefusal.add("Term < 6");
        }

        if (Period.between(request.getBirthdate(), LocalDate.now()).getYears() < 18) {
            reasonsRefusal.add("Client under 18 years");
        }

        if (!request.getEmail().matches("[\\w\\.]{2,50}@[\\w\\.]{2,20}")) {
            reasonsRefusal.add("Invalid email format");
        }

        if (!request.getPassportSeries().matches("\\d{4}")) {
            reasonsRefusal.add("Incorrect passport series");
        }

        if (!request.getPassportNumber().matches("\\d{6}")) {
            reasonsRefusal.add("Incorrect passport number");
        }

        if (reasonsRefusal.size() > 0) {
            String fails = Arrays.deepToString(reasonsRefusal.toArray());
            log.error("Verification failed: {}",  fails);
            throw new PreScoringException("Failed pre scoring: " + fails);
        }

        log.info("End process scoring. Client: {} {}", request.getFirstName(), request.getLastName());
    }
}
