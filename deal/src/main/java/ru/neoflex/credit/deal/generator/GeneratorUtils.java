package ru.neoflex.credit.deal.generator;

import ru.neoflex.credit.deal.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.neoflex.credit.deal.model.ApplicationStatusEnum.PREAPPROVAL;
import static ru.neoflex.credit.deal.model.ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC;
import static ru.neoflex.credit.deal.model.CreditStatus.CALCULATED;
import static ru.neoflex.credit.deal.model.EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED;

public class GeneratorUtils {
    public static LoanApplicationRequestDTO generateLoanApplicationRequestDTO() {
        return new LoanApplicationRequestDTO()
                .amount(BigDecimal.valueOf(900000))
                .term(6)
                .firstName("Mikhail")
                .middleName("Alexey")
                .lastName("Deev")
                .birthdate(LocalDate.of(2000, 6, 4))
                .passportSeries("123456")
                .passportNumber("123456");
    }

    public static Passport generatePassportSeriesNumber(LoanApplicationRequestDTO request) {
        return new Passport()
                .series(request.getPassportSeries())
                .number(request.getPassportNumber());
    }

    public static Client generateClient(LoanApplicationRequestDTO request, Passport passportClient) {
        return new Client()
                .setId(1L)
                .setLastName(request.getLastName())
                .setFirstName(request.getFirstName())
                .setMiddleName(request.getMiddleName())
                .setBirthDate(request.getBirthdate())
                .setEmail(request.getEmail())
                .setPassport(passportClient);
    }

    public static LoanOfferDTO generateLoanOfferDTO(LoanApplicationRequestDTO request) {
        return new LoanOfferDTO()
                .applicationId(1L)
                .requestedAmount(request.getAmount())
                .totalAmount(request.getAmount())
                .term(request.getTerm())
                .monthlyPayment(BigDecimal.valueOf(3000))
                .rate(BigDecimal.valueOf(1))
                .isInsuranceEnabled(true)
                .isSalaryClient(true);
    }

    public static List<ApplicationStatusHistoryDTO> generateStatusHistoryList() {
        return new ArrayList<>(List.of(
                new ApplicationStatusHistoryDTO()
                        .status(PREAPPROVAL)
                        .time(LocalDateTime.now())
                        .changeType(AUTOMATIC)
        ));
    }

    public static Application generateApplication(Client client, List<ApplicationStatusHistoryDTO> statusHistoryList, LoanOfferDTO loanOfferDTO) {
        return new Application()
                .id(1L)
                .client(client)
                .creationDate(LocalDate.now())
                .status(PREAPPROVAL)
                .statusHistory(statusHistoryList)
                .appliedOffer(loanOfferDTO);
    }

    public static EmploymentDTO generateEmploymentDTO() {
        return new EmploymentDTO()
                .employmentStatus(SELF_EMPLOYED)
                .employerINN("1234567891")
                .salary(new BigDecimal("60000"))
                .position(EmploymentDTO.PositionEnum.MID_MANAGER)
                .workExperienceTotal(40)
                .workExperienceCurrent(13);
    }

    public static List<PaymentScheduleElement> generatePaymentScheduleElementList() {

        PaymentScheduleElement paymentElement1 = new PaymentScheduleElement()
                .number(1)
                .date(LocalDate.of(2022, 7, 1))
                .totalPayment(BigDecimal.valueOf(336600.00))
                .interestPayment(BigDecimal.valueOf(82500.01))
                .debtPayment(BigDecimal.valueOf(254099.99))
                .remainingDebt(BigDecimal.valueOf(735900.01));
        PaymentScheduleElement paymentElement2 = new PaymentScheduleElement()
                .number(2)
                .date(LocalDate.of(2022, 8, 1))
                .totalPayment(BigDecimal.valueOf(336600.00))
                .interestPayment(BigDecimal.valueOf(61325.01))
                .debtPayment(BigDecimal.valueOf(275274.99))
                .remainingDebt(BigDecimal.valueOf(460625.02));
        PaymentScheduleElement paymentElement3 = new PaymentScheduleElement()
                .number(3)
                .date(LocalDate.of(2022, 9, 1))
                .totalPayment(BigDecimal.valueOf(336600.00))
                .interestPayment(BigDecimal.valueOf(38385.42))
                .debtPayment(BigDecimal.valueOf(298214.58))
                .remainingDebt(BigDecimal.valueOf(162410.44));

        return List.of(
                paymentElement1, paymentElement2, paymentElement3
        );
    }

    public static CreditDTO generateCreditDTO(LoanApplicationRequestDTO request, List<PaymentScheduleElement> paymentScheduleElementList) {
        return new CreditDTO()
                .amount(request.getAmount())
                .term(request.getTerm())
                .monthlyPayment(BigDecimal.valueOf(3000))
                .rate(BigDecimal.valueOf(1))
                .psk(BigDecimal.valueOf(1300000))
                .paymentSchedule(paymentScheduleElementList)
                .isInsuranceEnabled(true)
                .isSalaryClient(true);
    }

    public static Credit generateCredit(CreditDTO creditDTO) {
        return new Credit()
                .setId(1L)
                .setAmount(creditDTO.getAmount())
                .setTerm(creditDTO.getTerm())
                .setMonthlyPayment(creditDTO.getMonthlyPayment())
                .setRate(creditDTO.getRate())
                .setPsk(creditDTO.getPsk())
                .setPaymentSchedule(creditDTO.getPaymentSchedule())
                .setIsInsuranceEnabled(creditDTO.getIsInsuranceEnabled())
                .setIsSalaryClient(creditDTO.getIsSalaryClient())
                .setCreditStatus(CALCULATED);
    }
}
