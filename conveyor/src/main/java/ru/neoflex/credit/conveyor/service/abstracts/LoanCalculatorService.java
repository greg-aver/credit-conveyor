package ru.neoflex.credit.conveyor.service.abstracts;

import ru.neoflex.credit.conveyor.model.CreditDTO;
import ru.neoflex.credit.conveyor.model.ScoringDataDTO;

import java.math.BigDecimal;

public interface LoanCalculatorService {
    BigDecimal calculateTotalAmount(BigDecimal requestedAmount, boolean isInsuranceEnabled);
    BigDecimal calculateRate(boolean isInsuranceEnabled, boolean isSalaryClient);
    BigDecimal calculateMonthlyPayment(BigDecimal totalAmount, Integer term, BigDecimal rate);
    CreditDTO calculateCredit(ScoringDataDTO data);
}
