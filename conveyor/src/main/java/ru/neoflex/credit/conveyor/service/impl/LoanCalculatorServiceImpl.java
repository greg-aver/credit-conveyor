package ru.neoflex.credit.conveyor.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.conveyor.exception.ScoringException;
import ru.neoflex.credit.conveyor.model.CreditDTO;
import ru.neoflex.credit.conveyor.model.PaymentScheduleElement;
import ru.neoflex.credit.conveyor.model.ScoringDataDTO;
import ru.neoflex.credit.conveyor.service.abstracts.LoanCalculatorService;
import ru.neoflex.credit.conveyor.service.impl.factory.PaymentScheduleElementFactory;
import ru.neoflex.credit.conveyor.validator.ScoringService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.math.RoundingMode.CEILING;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanCalculatorServiceImpl implements LoanCalculatorService {
    @Value("${properties.baseRate}")
    private final String BASE_RATE;
    @Value("${properties.baseRate}")
    private BigDecimal CURRENT_RATE;
    @Value("${properties.baseInsurance}")
    private final String BASE_INSURANCE;
    @Value("${properties.percentageInsurance}")
    private final String PERCENTAGE_INSURANCE;
    @Value("${properties.rateDiscountSalaryClient}")
    private final String RATE_DISCOUNT_SALARY_CLIENT;
    @Value("${properties.rateDiscountInsuranceEnabled}")
    private final String RATE_DISCOUNT_INSURANCE_ENABLED;
    private final ScoringService validator;

    @Override
    public BigDecimal calculateTotalAmount(BigDecimal amount, boolean isInsuranceEnabled) {
        log.info("Start calculating the total amount insurance included or not");
        log.info("totalAmount = {}", amount);
        if (isInsuranceEnabled) {
            log.debug("Insurance included");
            BigDecimal insuranceCost = new BigDecimal(BigInteger.ZERO);

            insuranceCost = insuranceCost.add(amount.multiply(new BigDecimal(PERCENTAGE_INSURANCE)));

            log.debug("insuranceCost = {}", insuranceCost);
            amount = amount.add(insuranceCost);
        } else {
            log.debug("Insurance NOT included");
        }
        log.info("End calculate total amount. Amount = {}", amount);
        return amount;
    }

    @Override
    public BigDecimal calculateRate(boolean isInsuranceEnabled, boolean isSalaryClient) {
        log.info("Start calculate rate");

        BigDecimal currentRate = new BigDecimal(CURRENT_RATE.toString());
        log.debug("currentRate = {}", currentRate);

        if (isInsuranceEnabled) {
            currentRate = currentRate.subtract(new BigDecimal(RATE_DISCOUNT_INSURANCE_ENABLED));
            log.debug("Insurance enabled. Rate downgrade by {}", RATE_DISCOUNT_INSURANCE_ENABLED);
        }
        if (isSalaryClient) {
            currentRate = currentRate.subtract(new BigDecimal(RATE_DISCOUNT_SALARY_CLIENT));
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
        log.info("totalAmount = {}, term = {}, rate = {}", totalAmount, term, rate);

        BigDecimal rateMonthly = rate.divide(BigDecimal.valueOf(12), 10, CEILING)
                .divide(BigDecimal.valueOf(100), 10, CEILING);
        log.debug("rateMonthly = {}", rateMonthly);

        //  intermediateNumber = (1 + i)^n
        BigDecimal intermediateNumber = rateMonthly.add(BigDecimal.ONE).pow(term);
        log.debug("intermediateNumber = {}", intermediateNumber);

        BigDecimal numerator = intermediateNumber.multiply(rateMonthly);
        log.debug("numerator = {}", numerator);

        BigDecimal denominator = intermediateNumber.subtract(BigDecimal.ONE);
        log.debug("denominator = {}", denominator);

        BigDecimal annuityRatio = numerator.divide(denominator, 2, CEILING);
        log.debug("annuity ratio = {}", annuityRatio);

        BigDecimal result = annuityRatio.multiply(totalAmount).setScale(2, CEILING);
        log.info("End calculate monthly payment. Result = {}", result);

        return result;
    }

    @Override
    public CreditDTO calculateCredit(ScoringDataDTO scoringData) throws ScoringException {
        CURRENT_RATE = validator.scoring(scoringData, BASE_RATE);
        BigDecimal requestedAmount = scoringData.getAmount();
        boolean isInsuranceEnabled = scoringData.getIsInsuranceEnabled();
        boolean isSalaryClient = scoringData.getIsSalaryClient();
        BigDecimal totalAmount = calculateTotalAmount(requestedAmount, isInsuranceEnabled);
        Integer term = scoringData.getTerm();
        BigDecimal rate = calculateRate(isInsuranceEnabled, isSalaryClient);
        BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount, term, rate);
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
        BigDecimal remainingDebt = totalAmount.setScale(2, CEILING);

        for (int i = 1; i <= term; i++) {

            BigDecimal interestPayment = calculateInterestPayment(remainingDebt).setScale(2, CEILING);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment).setScale(2, CEILING);
            datePayment = datePayment.plusMonths(1L);
            remainingDebt = remainingDebt.subtract(debtPayment).setScale(2, CEILING);
            PaymentScheduleElement paymentScheduleElement = PaymentScheduleElementFactory.createPaymentScheduleElement(
                    i, datePayment, monthlyPayment, interestPayment, debtPayment, remainingDebt
            );
            resultList.add(paymentScheduleElement);
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
                .divide(BigDecimal.valueOf(12), 2, CEILING);
        log.debug("termYears = {}", termYears);

        BigDecimal paymentAmount = paymentSchedule
                .stream()
                .map(PaymentScheduleElement::getTotalPayment)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        log.debug("Payment amount = {}", paymentAmount);

        BigDecimal numerator = paymentAmount.divide(requestedAmount, 2, CEILING)
                .subtract(BigDecimal.ONE);
        BigDecimal psk = numerator
                .divide(termYears, 2, CEILING)
                .divide(BigDecimal.valueOf(100), 2, CEILING)
                .divide(termYears, 2, CEILING);
//                .setScale(2);
        log.info("End calculate psk. PSK = {}", psk);
        return psk;
    }

    private BigDecimal calculateInterestPayment(BigDecimal remainingDebt) {
        BigDecimal rateAbsolute = CURRENT_RATE
                .divide(BigDecimal.valueOf(100), CEILING);
        BigDecimal monthlyRate = rateAbsolute.divide(BigDecimal.valueOf(12), 10, CEILING);
        return monthlyRate.multiply(remainingDebt).setScale(2, CEILING);
    }
}
