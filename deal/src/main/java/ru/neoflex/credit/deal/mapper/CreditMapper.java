package ru.neoflex.credit.deal.mapper;

import org.mapstruct.Mapper;
import ru.neoflex.credit.deal.model.Credit;
import ru.neoflex.credit.deal.model.CreditDTO;

@Mapper(componentModel = "spring")
public abstract class CreditMapper {
    public CreditDTO toDto(Credit credit) {
        return new CreditDTO()
                .id(credit.getId())
                .amount(credit.getAmount())
                .isInsuranceEnabled(credit.getIsInsuranceEnabled())
                .isSalaryClient(credit.getIsSalaryClient())
                .monthlyPayment(credit.getMonthlyPayment())
                .psk(credit.getPsk())
                .rate(credit.getRate())
                .term(credit.getTerm())
                .paymentSchedule(credit.getPaymentSchedule());
    }
}
