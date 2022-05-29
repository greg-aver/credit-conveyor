package ru.neoflex.credit.conveyor.service.abstracts.factory;

import ru.neoflex.credit.conveyor.model.PaymentScheduleElement;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FactoryPaymentScheduleElement {
    PaymentScheduleElement createPaymentScheduleElement(
            Integer number,
            LocalDate date,
            BigDecimal totalPayment,
            BigDecimal interestedPayment,
            BigDecimal debtPayment,
            BigDecimal remainingDebt
    );
}
