package ru.neoflex.credit.conveyor.service.impl.factory;

import lombok.experimental.UtilityClass;
import ru.neoflex.credit.conveyor.model.PaymentScheduleElement;

import java.math.BigDecimal;
import java.time.LocalDate;
@UtilityClass
public class PaymentScheduleElementFactory {

    public PaymentScheduleElement createPaymentScheduleElement(Integer number, LocalDate date, BigDecimal totalPayment, BigDecimal interestedPayment, BigDecimal debtPayment, BigDecimal remainingDebt) {
        return new PaymentScheduleElement()
                .number(number)
                .date(date)
                .totalPayment(totalPayment)
                .interestPayment(interestedPayment)
                .debtPayment(debtPayment)
                .remainingDebt(remainingDebt);
    }
}
