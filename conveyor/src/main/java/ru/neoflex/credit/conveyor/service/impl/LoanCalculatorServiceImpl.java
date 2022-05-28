package ru.neoflex.credit.conveyor.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.conveyor.model.CreditDTO;
import ru.neoflex.credit.conveyor.model.EmploymentDTO;
import ru.neoflex.credit.conveyor.model.ScoringDataDTO;
import ru.neoflex.credit.conveyor.service.abstracts.LoanCalculatorService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class LoanCalculatorServiceImpl implements LoanCalculatorService {
    @Value("${properties.baseRate}")
    private final String BASE_RATE;
    private BigDecimal CURRENT_RATE = new BigDecimal(BASE_RATE);

    @Override
    public BigDecimal calculateTotalAmount(BigDecimal requestedAmount, boolean isInsuranceEnabled) {
        return null;
    }

    @Override
    public BigDecimal calculateRate(boolean isInsuranceEnabled, boolean isSalaryClient) {
        return null;
    }

    @Override
    public BigDecimal calculateMonthlyPayment(BigDecimal totalAmount, Integer term, BigDecimal rate) {
        return null;
    }

    @Override
    public CreditDTO calculateCredit(ScoringDataDTO data) {
        return null;
    }

    /* Правила скоринга:

    1. Рабочий статус: Безработный → отказ; Самозанятый → ставка увеличивается на 1; Владелец бизнеса → ставка увеличивается на 3
    2. Позиция на работе: Менеджер среднего звена → ставка уменьшается на 2; Топ-менеджер → ставка уменьшается на 4
    3. Сумма займа больше, чем 20 зарплат → отказ
    4. Семейное положение: Замужем/женат → ставка уменьшается на 3; Разведен → ставка увеличивается на 1
    5. Количество иждивенцев больше 1 → ставка увеличивается на 1
    6. Возраст менее 20 или более 60 лет → отказ
    7. Пол: Женщина, возраст от 35 до 60 лет → ставка уменьшается на 3; Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3; Не бинарный → ставка увеличивается на 3
    8. Стаж работы: Общий стаж менее 12 месяцев → отказ; Текущий стаж менее 3 месяцев → отказ */
    boolean scoring(ScoringDataDTO data) {
        EmploymentDTO employment = data.getEmployment();
        boolean scoring = true;
        BigDecimal currentRate = new BigDecimal(BASE_RATE);

        switch (employment.getEmploymentStatus()) {
            case UNEMPLOYED:
                scoring = false;
                break;
            case SELF_EMPLOYED:
                currentRate.add(BigDecimal.ONE);
                break;
            case BUSINESS_OWNER:
                currentRate.add(BigDecimal.valueOf(3));
                break;
        }

        switch (employment.getPosition()) {
            case MID_MANAGER:
                currentRate.subtract(BigDecimal.valueOf(2));
                break;
            case TOP_MANAGER:
                currentRate.subtract(BigDecimal.valueOf(4));
                break;
        }

        int clientAge = Period.between(data.getBirthdate(), LocalDate.now()).getYears();

        if (
            data.getAmount()
                    .compareTo(employment.getSalary().multiply(BigDecimal.valueOf(20))) > 0
                && clientAge < 20
                && clientAge > 60
                && employment.getWorkExperienceTotal() < 12
                && employment.getWorkExperienceCurrent() < 3
        ) {
            scoring = false;
        }

        switch (data.getGender()) {
            case NON_BINARY:
                currentRate.add(BigDecimal.valueOf(3));
                break;
            case MALE:
                if (clientAge >= 30 && clientAge <= 55) {
                    currentRate.subtract(BigDecimal.valueOf(3));
                }
                break;
            case FEMALE:
                if (clientAge >= 35 && clientAge <= 60) {
                    currentRate.subtract(BigDecimal.valueOf(3));
                }
                break;
        }

        switch (data.getMaritalStatus()) {
            case MARRIED:
                currentRate.subtract(BigDecimal.valueOf(3));
                break;
            case DIVORCED:
                currentRate.add(BigDecimal.ONE);
        }

        if (data.getDependentAmount() > 1) {
            currentRate.add(BigDecimal.ONE);
        }

        CURRENT_RATE = currentRate;
        return scoring;
    }
}
