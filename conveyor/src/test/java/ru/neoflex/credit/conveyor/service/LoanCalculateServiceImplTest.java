package ru.neoflex.credit.conveyor.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import ru.neoflex.credit.conveyor.model.CreditDTO;
import ru.neoflex.credit.conveyor.model.EmploymentDTO;
import ru.neoflex.credit.conveyor.model.PaymentScheduleElement;
import ru.neoflex.credit.conveyor.model.ScoringDataDTO;
import ru.neoflex.credit.conveyor.service.impl.LoanCalculatorServiceImpl;
import ru.neoflex.credit.conveyor.service.impl.factory.PaymentScheduleElementFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class LoanCalculateServiceImplTest {

    @InjectMocks
    private LoanCalculatorServiceImpl service;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(service, "BASE_RATE", "11");
        ReflectionTestUtils.setField(service, "CURRENT_RATE", new BigDecimal("11"));
        ReflectionTestUtils.setField(service, "BASE_INSURANCE", "10000");
        ReflectionTestUtils.setField(service, "PERCENTAGE_INSURANCE", "0.1");
        ReflectionTestUtils.setField(service, "RATE_DISCOUNT_SALARY_CLIENT", "1");
        ReflectionTestUtils.setField(service, "RATE_DISCOUNT_INSURANCE_ENABLED", "2");
    }

    @Test
    public void calculateTotalAmount_true() {
        BigDecimal totalAmount = new BigDecimal("10000");
        boolean isInsuranceEnabled = true;

        BigDecimal actualResult = service.calculateTotalAmount(totalAmount, isInsuranceEnabled);
        assertEquals(BigDecimal.valueOf(11000.0), actualResult);
    }

    @Test
    public void calculateTotalAmount_false() {
        BigDecimal totalAmount = new BigDecimal("10000");
        boolean isInsuranceEnabled = false;

        BigDecimal actualResult = service.calculateTotalAmount(totalAmount, isInsuranceEnabled);
        assertEquals(BigDecimal.valueOf(10000), actualResult);
    }

    @Test
    public void calculateRate_true() {
        boolean isInsuranceEnabled = true;
        boolean isSalaryClient = true;

        BigDecimal rateActual = service.calculateRate(isInsuranceEnabled, isSalaryClient);
        assertEquals(BigDecimal.valueOf(8), rateActual);
    }

    @Test
    public void calculateRate_false() {
        boolean isInsuranceEnabled = false;
        boolean isSalaryClient = false;

        BigDecimal rateActual = service.calculateRate(isInsuranceEnabled, isSalaryClient);
        assertEquals(BigDecimal.valueOf(11), rateActual);
    }

    @Test
    public void calculateMonthlyPayment() {
        BigDecimal totalAmount = new BigDecimal("900000");
        Integer term = 24;
        BigDecimal rate = new BigDecimal("16.0");

        BigDecimal monthlyPaymentActual = service.calculateMonthlyPayment(totalAmount, term, rate);
        assertEquals(new BigDecimal("45000.00"), monthlyPaymentActual);
    }

    @Test
    public void factory() {
        PaymentScheduleElement paymentElementExpected = new PaymentScheduleElement()
                .number(1)
                .date(LocalDate.of(2022, 7, 1))
                .totalPayment(BigDecimal.valueOf(336600.00))
                .interestPayment(BigDecimal.valueOf(82500.01))
                .debtPayment(BigDecimal.valueOf(254099.99))
                .remainingDebt(BigDecimal.valueOf(735900.01));
        PaymentScheduleElement paymentElementActual = PaymentScheduleElementFactory.createPaymentScheduleElement(
                1,
                LocalDate.of(2022, 7, 1),
                BigDecimal.valueOf(336600.00),
                BigDecimal.valueOf(82500.01),
                BigDecimal.valueOf(254099.99),
                BigDecimal.valueOf(735900.01)
        );
        assertEquals(paymentElementExpected, paymentElementActual);
        assertEquals(paymentElementExpected.getNumber(), paymentElementActual.getNumber());
        assertEquals(paymentElementExpected.getDate(), paymentElementActual.getDate());
        assertEquals(paymentElementExpected.getTotalPayment(), paymentElementActual.getTotalPayment());
        assertEquals(paymentElementExpected.getInterestPayment(), paymentElementActual.getInterestPayment());
        assertEquals(paymentElementExpected.getDebtPayment(), paymentElementActual.getDebtPayment());
        assertEquals(paymentElementExpected.getRemainingDebt(), paymentElementActual.getRemainingDebt());
    }

    @Test
    public void calculateCredit() {
        EmploymentDTO employment = new EmploymentDTO()
                .employmentStatus(EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED)
                .employerINN("00085866")
                .salary(new BigDecimal("60000"))
                .position(EmploymentDTO.PositionEnum.MID_MANAGER)
                .workExperienceTotal(40)
                .workExperienceCurrent(13);

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO()
                .amount(BigDecimal.valueOf(900000))
                .term(3)
                .firstName("Mikhail")
                .middleName("Alexey")
                .lastName("Deev")
                .gender(ScoringDataDTO.GenderEnum.MALE)
                .birthdate(LocalDate.of(2000, 6, 4))
                .passportSeries("0808")
                .passportNumber("010203")
                .passportIssueDate(LocalDate.of(2015, 6, 4))
                .passportIssueBranch("Hospitable department of Dagestan")
                .maritalStatus(ScoringDataDTO.MaritalStatusEnum.SINGLE)
                .dependentAmount(900000)
                .employment(employment)
                .account("003456")
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

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
        List<PaymentScheduleElement> paymentScheduleElementList = List.of(
                paymentElement1, paymentElement2, paymentElement3
        );

        CreditDTO creditDTOActual = service.calculateCredit(scoringDataDTO);

        assertEquals(BigDecimal.valueOf(990000.0), creditDTOActual.getAmount());
        assertEquals(Integer.valueOf(3), creditDTOActual.getTerm());
        assertEquals(new BigDecimal("336600.00"), creditDTOActual.getMonthlyPayment());
        assertEquals(BigDecimal.valueOf(8), creditDTOActual.getRate());
        assertEquals(BigDecimal.valueOf(0.04), creditDTOActual.getPsk());
        assertEquals(true, creditDTOActual.getIsInsuranceEnabled());
        assertEquals(true, creditDTOActual.getIsSalaryClient());
        assertEquals(paymentScheduleElementList.get(0).getRemainingDebt(), creditDTOActual.getPaymentSchedule().get(0).getRemainingDebt());
    }

}
