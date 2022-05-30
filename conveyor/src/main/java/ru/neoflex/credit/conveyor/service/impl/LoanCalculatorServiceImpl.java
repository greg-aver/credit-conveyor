package ru.neoflex.credit.conveyor.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.conveyor.exception.ScoringException;
import ru.neoflex.credit.conveyor.model.CreditDTO;
import ru.neoflex.credit.conveyor.model.EmploymentDTO;
import ru.neoflex.credit.conveyor.model.PaymentScheduleElement;
import ru.neoflex.credit.conveyor.model.ScoringDataDTO;
import ru.neoflex.credit.conveyor.service.abstracts.LoanCalculatorService;
import ru.neoflex.credit.conveyor.service.abstracts.factory.FactoryPaymentScheduleElement;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanCalculatorServiceImpl implements LoanCalculatorService {
    @Value("${properties.baseRate}")
    private final String BASE_RATE;
    private BigDecimal CURRENT_RATE = new BigDecimal(BASE_RATE);
    @Value("${properties.baseInsurance}")
    private final String BASE_INSURANCE;
    @Value("${properties.percentageInsurance}")
    private final String PERCENTAGE_INSURANCE;
    @Value("${properties.rateDiscountSalaryClient}")
    private final String RATE_DISCOUNT_SALARY_CLIENT;
    @Value("${properties.rateDiscountInsuranceEnabled}")
    private final String RATE_DISCOUNT_INSURANCE_ENABLED;

    private final FactoryPaymentScheduleElement factoryPayment;
    @Override
    public BigDecimal calculateTotalAmount(BigDecimal amount, boolean isInsuranceEnabled) {
        if (isInsuranceEnabled) {
            BigDecimal insuranceCost = new BigDecimal(BigInteger.ZERO);
            insuranceCost.add(amount.multiply(new BigDecimal(PERCENTAGE_INSURANCE)));
            amount.add(insuranceCost);
        }
        return amount;
    }

    @Override
    public BigDecimal calculateRate(boolean isInsuranceEnabled, boolean isSalaryClient) {
        log.info("Start calculate rate");

        BigDecimal currentRate = new BigDecimal(CURRENT_RATE.toString());
        log.debug("currentRate = {}", currentRate);

        if (isInsuranceEnabled) {
            currentRate.subtract(new BigDecimal(RATE_DISCOUNT_INSURANCE_ENABLED));
            log.debug("Insurance enabled. Rate downgrade by {}", RATE_DISCOUNT_INSURANCE_ENABLED);
        }
        if (isSalaryClient) {
            currentRate.subtract(new BigDecimal(RATE_DISCOUNT_SALARY_CLIENT));
            log.debug("isSalaryClient = true. Rate downgrade by {}", RATE_DISCOUNT_SALARY_CLIENT);
        }
        CURRENT_RATE = currentRate;
        log.info("End calculate current rate. Rate = {}", currentRate);
        return currentRate;
    }

    /*
    * Ежемесячный платеж = Коэффициент аннуитета * Сумма кредита
    * Коэффициент = (i * (1 + i)^n) / ((1 + i)^n - 1)
    * i - процентная ставка по кредиту в месяц
    * i = годовая ставка / 12 месяцев
    * n - количество месяцев, за которые нужно погасить кредит
    **/
    @Override
    public BigDecimal calculateMonthlyPayment(BigDecimal totalAmount, Integer term, BigDecimal rate) {
        log.info("Start calculate monthly payment");

        BigDecimal rateMonthly = rate.divide(BigDecimal.valueOf(12));
        log.debug("rateMonthly = {}", rateMonthly);

    //  intermediateNumber = (1 + i)^n
        BigDecimal intermediateNumber = rateMonthly.add(BigDecimal.ONE).pow(term);
        log.debug("intermediateNumber = {}", intermediateNumber);

        BigDecimal numerator = intermediateNumber.multiply(rateMonthly);
        log.debug("numerator = {}", numerator);

        BigDecimal denominator = intermediateNumber.subtract(BigDecimal.ONE);
        log.debug("denominator = {}", denominator);

        BigDecimal result = numerator.divide(denominator).multiply(totalAmount);
        log.info("End calculate monthly payment. Result = {}", result);

        return result;
    }

    @Override
    public CreditDTO calculateCredit(ScoringDataDTO scoringData) {
        scoring(scoringData);
        BigDecimal requestedAmount = scoringData.getAmount();
        boolean isInsuranceEnabled = scoringData.getIsInsuranceEnabled();
        boolean isSalaryClient = scoringData.getIsSalaryClient();
        BigDecimal totalAmount = calculateTotalAmount(requestedAmount, isInsuranceEnabled);
        Integer term = scoringData.getTerm();
        BigDecimal rate = calculateRate(isInsuranceEnabled, isSalaryClient);
        BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount,term,rate);
        List<PaymentScheduleElement> paymentScheduleElementList = calculateListPaymentSchedule(totalAmount, term, monthlyPayment, rate);
        BigDecimal psk = calculatePSK(requestedAmount, term, paymentScheduleElementList);
        return new CreditDTO()
                .amount(totalAmount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .psk(psk)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .paymentSchedule(paymentScheduleElementList);
    }

    private List<PaymentScheduleElement> calculateListPaymentSchedule(
            BigDecimal totalAmount, Integer term, BigDecimal monthlyPayment, BigDecimal rate
    ) {
        ArrayList<PaymentScheduleElement> resultList = new ArrayList<>();
        LocalDate datePayment = LocalDate.now();
        BigDecimal remainingDebt = totalAmount.setScale(2);

        for (int i = 1; i <= term; i++) {

            BigDecimal interestPayment = calculateInterestPayment(remainingDebt);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);
            datePayment.plusMonths(1L);
            remainingDebt = remainingDebt.subtract(debtPayment);
            factoryPayment.createPaymentScheduleElement(
                    i, datePayment, monthlyPayment, interestPayment, debtPayment, remainingDebt
            );
        }
        return resultList;
    }

    /* Формула рассчета полной стоимости кредита:
    * ПСК = (S/So - 1) / (100 * n)
    * n - срок погашения в годах
    * S - сумма всех кредитных платежей
    * So - сумма полученная от банка
    * */

    private BigDecimal calculatePSK(
            BigDecimal requestedAmount, Integer term, List<PaymentScheduleElement> paymentSchedule
    ) {
        log.info("Start calculate PSK");

        BigDecimal termYears = new BigDecimal(term)
                .divide(BigDecimal.valueOf(12))
                .setScale(2);
        log.debug("termYears = {}", termYears);

        BigDecimal paymentAmount = paymentSchedule
                .stream()
                .map(PaymentScheduleElement::getTotalPayment)
                .reduce(BigDecimal::add).orElse(BigDecimal.ONE);
        log.debug("Payment amount = {}", paymentAmount);

        BigDecimal numerator = paymentAmount.divide(requestedAmount)
                .subtract(BigDecimal.ONE);
        BigDecimal psk = numerator
                .divide(termYears)
                .divide(BigDecimal.valueOf(100))
                .divide(termYears)
                .setScale(2);
        log.info("End calculate psk. PSK = {}", psk);
        return psk;
    }

    private BigDecimal calculateInterestPayment(BigDecimal remainingDebt) {
        BigDecimal rateAbsolute = new BigDecimal("#.##");
        rateAbsolute.add(CURRENT_RATE)
                .divide(BigDecimal.valueOf(100));
        return rateAbsolute.multiply(remainingDebt);
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
    private void scoring(ScoringDataDTO scoringData) {
        EmploymentDTO employment = scoringData.getEmployment();
        ArrayList<String> reasonsRefusal = new ArrayList<>();
        BigDecimal currentRate = new BigDecimal(BASE_RATE);

        log.info("Scoring start");

        switch (employment.getEmploymentStatus()) {
            case UNEMPLOYED:
                reasonsRefusal.add("Denied a loan: Client unemployed");
                break;
            case SELF_EMPLOYED:
                currentRate.add(BigDecimal.ONE);
                log.debug("Client is self employed. Rate increased by 1");
                break;
            case BUSINESS_OWNER:
                currentRate.add(BigDecimal.valueOf(3));
                log.debug("Client is business owner. Rate increased by 3");
                break;
        }

        switch (employment.getPosition()) {
            case MID_MANAGER:
                currentRate.subtract(BigDecimal.valueOf(2));
                log.debug("Client is middle manager. Rate reduced by 3");
                break;
            case TOP_MANAGER:
                currentRate.subtract(BigDecimal.valueOf(4));
                log.debug("Client is top manager. Rate reduced by 4");
                break;
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
                currentRate.add(BigDecimal.valueOf(3));
                log.debug("The client has a non-binary gender. Rate increased by 3");
                break;
            case MALE:
                if (clientAge >= 30 && clientAge <= 55) {
                    currentRate.subtract(BigDecimal.valueOf(3));
                    log.debug("The client is male and between 30 and 55 years of age. Rate reduced 3");
                }
                break;
            case FEMALE:
                if (clientAge >= 35 && clientAge <= 60) {
                    currentRate.subtract(BigDecimal.valueOf(3));
                    log.debug("The client is female and between 35 and 60 years of age. Rate reduced 3");
                }
                break;
        }

        switch (scoringData.getMaritalStatus()) {
            case MARRIED:
                currentRate.subtract(BigDecimal.valueOf(3));
                log.debug("The client is married. Rate reduced 3");
                break;
            case DIVORCED:
                currentRate.add(BigDecimal.ONE);
                log.debug("The client is divorced. Rate increased 1");
                break;
        }

        if (scoringData.getDependentAmount() > 1) {
            currentRate.add(BigDecimal.ONE);
        }

        CURRENT_RATE = currentRate;

        log.info("End process scoring. Client: {} {}", scoringData.getFirstName(), scoringData.getLastName());

        if (reasonsRefusal.size() > 0) {
            String problems = Arrays.deepToString(reasonsRefusal.toArray());
            log.error("Denied a loan {}", problems);
            throw new ScoringException(problems);
        }

    }
}
