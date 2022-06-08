package ru.neoflex.credit.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.application.feign.DealFeignClient;
import ru.neoflex.credit.application.service.abstracts.ApplicationService;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;

import java.util.List;

@Service
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

    }
}
