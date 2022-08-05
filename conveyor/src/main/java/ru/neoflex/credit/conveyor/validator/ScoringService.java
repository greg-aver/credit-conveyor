package ru.neoflex.credit.conveyor.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.neoflex.credit.conveyor.exception.ScoringException;
import ru.neoflex.credit.conveyor.model.EmploymentDTO;
import ru.neoflex.credit.conveyor.model.ScoringDataDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;

@Component
@Slf4j
public class ScoringService {
    /* Правила скоринга:

    1. Рабочий статус: Безработный → отказ; Самозанятый → ставка увеличивается на 1; Владелец бизнеса → ставка увеличивается на 3
    2. Позиция на работе: Менеджер среднего звена → ставка уменьшается на 2; Топ-менеджер → ставка уменьшается на 4
    3. Сумма займа больше, чем 20 зарплат → отказ
    4. Семейное положение: Замужем/женат → ставка уменьшается на 3; Разведен → ставка увеличивается на 1
    5. Количество иждивенцев больше 1 → ставка увеличивается на 1
    6. Возраст менее 20 или более 60 лет → отказ
    7. Пол: Женщина, возраст от 35 до 60 лет → ставка уменьшается на 3; Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3; Не бинарный → ставка увеличивается на 3
    8. Стаж работы: Общий стаж менее 12 месяцев → отказ; Текущий стаж менее 3 месяцев → отказ */

    public BigDecimal scoring(ScoringDataDTO scoringData, String baseRate) {
        EmploymentDTO employment = scoringData.getEmployment();
        ArrayList<String> reasonsRefusal = new ArrayList<>();
        BigDecimal currentRate = new BigDecimal(baseRate);

        log.info("Scoring start");

        switch (employment.getEmploymentStatus()) {
            case UNEMPLOYED:
                reasonsRefusal.add("Denied a loan: Client unemployed");
                break;
            case SELF_EMPLOYED:
                currentRate = currentRate.add(BigDecimal.ONE);
                log.debug("Client is self employed. Rate increased by 1");
                break;
            case EMPLOYED:
                log.debug("Client is EMPLOYED");
                break;
            case BUSINESS_OWNER:
                currentRate = currentRate.add(BigDecimal.valueOf(3));
                log.debug("Client is business owner. Rate increased by 3");
                break;
        }

        if (employment.getPosition() == null) {
            reasonsRefusal.add("Denied a loan: Client unemployed");
        } else {
            switch (employment.getPosition()) {
                case MID_MANAGER:
                    currentRate = currentRate.subtract(BigDecimal.valueOf(2));
                    log.debug("Client is middle manager. Rate reduced by 3");
                    break;
                case TOP_MANAGER:
                    currentRate = currentRate.subtract(BigDecimal.valueOf(4));
                    log.debug("Client is top manager. Rate reduced by 4");
                    break;
            }
        }
        int clientAge = Period.between(scoringData.getBirthdate(), LocalDate.now()).getYears();

        if (scoringData.getAmount()
                .compareTo(employment.getSalary().multiply(BigDecimal.valueOf(20))) > 0) {
            reasonsRefusal.add("Denied a loan: The requested loan exceeds 20 salaries");
        }

        if (clientAge < 20) {
            reasonsRefusal.add("Denied a loan: Client under 20");
        }

        if (clientAge > 60) {
            reasonsRefusal.add("Denied a loan: Client over 60");
        }

        if (employment.getWorkExperienceTotal() < 12) {
            reasonsRefusal.add("Denied a loan: Total work experience less than 1 year");
        }

        if (employment.getWorkExperienceCurrent() < 3) {
            reasonsRefusal.add("Denied a loan: At least 3 months work experience in current position");
        }

        switch (scoringData.getGender()) {
            case NON_BINARY:
                currentRate = currentRate.add(BigDecimal.valueOf(3));
                log.debug("The client has a non-binary gender. Rate increased by 3");
                break;
            case MALE:
                if (clientAge >= 30 && clientAge <= 55) {
                    currentRate = currentRate.subtract(BigDecimal.valueOf(3));
                    log.debug("The client is male and between 30 and 55 years of age. Rate reduced 3");
                }
                break;
            case FEMALE:
                if (clientAge >= 35 && clientAge <= 60) {
                    currentRate = currentRate.subtract(BigDecimal.valueOf(3));
                    log.debug("The client is female and between 35 and 60 years of age. Rate reduced 3");
                }
                break;
        }

        switch (scoringData.getMaritalStatus()) {
            case MARRIED:
                currentRate = currentRate.subtract(BigDecimal.valueOf(3));
                log.debug("The client is married. Rate reduced 3");
                break;
            case DIVORCED:
                currentRate = currentRate.add(BigDecimal.ONE);
                log.debug("The client is divorced. Rate increased 1");
                break;
        }

        if (scoringData.getDependentAmount() > 1) {
            currentRate = currentRate.add(BigDecimal.ONE);
        }

        log.info("End process scoring. Client: {} {}", scoringData.getFirstName(), scoringData.getLastName());

        if (reasonsRefusal.size() > 0) {
            String epicFail = Arrays.deepToString(reasonsRefusal.toArray());
            log.error("Denied a loan {}", epicFail);
            throw new ScoringException(epicFail);
        }

        return currentRate;
    }
}
