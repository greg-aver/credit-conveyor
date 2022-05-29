package ru.neoflex.credit.conveyor.service.impl.factory;

import ru.neoflex.credit.conveyor.model.PaymentScheduleElement;
import ru.neoflex.credit.conveyor.service.abstracts.factory.FactoryPaymentScheduleElement;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FactoryPaymentScheduleElementImpl implements FactoryPaymentScheduleElement {
    @Override
    public PaymentScheduleElement createPaymentScheduleElement(Integer number, LocalDate date, BigDecimal totalPayment, BigDecimal interestedPayment, BigDecimal debtPayment, BigDecimal remainingDebt) {
        return new PaymentScheduleElement()
                .number(number)
                .date(date)
                .totalPayment(totalPayment)
                .interestedPayment(interestedPayment)
                .debtPayment(debtPayment)
                .remainingDebt(remainingDebt);
    }
}
