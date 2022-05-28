package ru.neoflex.credit.conveyor.service.impl;

import org.springframework.stereotype.Service;
import ru.neoflex.credit.conveyor.model.CreditDTO;
import ru.neoflex.credit.conveyor.model.ScoringDataDTO;
import ru.neoflex.credit.conveyor.service.abstracts.LoanCalculatorService;

import java.math.BigDecimal;

@Service
public class LoanCalculatorServiceImpl implements LoanCalculatorService {
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
}
