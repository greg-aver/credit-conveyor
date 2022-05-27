package ru.neoflex.credit.conveyor.service.abstracts;

import java.math.BigDecimal;

public interface LoanCalculatorService {
    BigDecimal calculateTotalAmount(BigDecimal requestedAmount, boolean isInsuranceEnabled);
    BigDecimal calculateRate(boolean isInsuranceEnabled, boolean isSalaryClient);
    BigDecimal calculateMonthlyPayment(BigDecimal totalAmount, Integer term, BigDecimal rate);
}
