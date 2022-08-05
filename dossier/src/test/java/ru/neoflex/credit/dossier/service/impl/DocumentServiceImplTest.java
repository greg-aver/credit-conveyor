package ru.neoflex.credit.dossier.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import ru.neoflex.credit.deal.model.*;
import ru.neoflex.credit.dossier.feign.DealFeignClient;

import java.io.FileWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.neoflex.credit.deal.model.ApplicationStatusEnum.PREAPPROVAL;
import static ru.neoflex.credit.deal.model.ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC;
import static ru.neoflex.credit.deal.model.EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED;

@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceImplTest {
    @Mock
    private DealFeignClient dealFeignClient;
    @Mock
    private FileWriter fileWriter;
    @Value("${document.text.contract}")
    private String CONTRACT_TEXT;
    @Value("${document.text.credit-application}")
    private String CREDIT_APPLICATION_TEXT;
    @Value("${document.text.loan-payment-schedule}")
    private String LOAN_PAYMENT_SCHEDULE_TEXT;
    @InjectMocks
    private DocumentServiceImpl documentService;
    private ApplicationDTO applicationDTO;

    @Before
    public void setUp() {
        CONTRACT_TEXT = " >\n" +
                "    Credit contract ${creditId}\n" +
                "    Full name - ${clientFullName}\n" +
                "    Passport - ${clientPassport}\n" +
                "    Credit -\n" +
                "    amount - ${creditAmount}\n" +
                "    term - ${creditTerm}\n" +
                "    monthly payment - ${monthlyPayment}\n" +
                "    rate - ${rate}\n" +
                "    psk - ${psk}\n" +
                "    credit date - ${creditDate}\n" +
                "    additional services -\n" +
                "    insurance - ${isInsuranceEnabled}\n" +
                "    salary client - ${isSalaryClient}";
        CREDIT_APPLICATION_TEXT = " >\n" +
                "      Credit application ${applicationId}\n" +
                "        Client -\n" +
                "          full name - ${clientFullName}\n" +
                "          birthdate - ${clientBirthdate}\n" +
                "          gender - ${clientGender}\n" +
                "          passport - ${clientPassport}\n" +
                "          email - ${clientEmail}\n" +
                "          martial status - ${clientMartialStatus}\n" +
                "          dependent amount - ${clientDependentAmount}\n" +
                "          Employment -\n" +
                "            employment status - ${employmentStatus}\n" +
                "            employer INN - ${employerINN}\n" +
                "            salary - ${employmentSalary}\n" +
                "            employment position - ${employmentPosition}\n" +
                "            work experience total - ${employmentWorkExperienceTotal}\n" +
                "            work experience current - ${employmentWorkExperienceCurrent}";
        LOAN_PAYMENT_SCHEDULE_TEXT = " >\n" +
                "    Payment schedule for credit contract ${creditId}\n" +
                "    Client's full name - ${clientFullName}\n" +
                "    Payment schedule -\n" +
                "    ${paymentSchedule}";

        ReflectionTestUtils.setField(documentService, "CONTRACT_TEXT", CONTRACT_TEXT);
        ReflectionTestUtils.setField(documentService, "CREDIT_APPLICATION_TEXT", CREDIT_APPLICATION_TEXT);
        ReflectionTestUtils.setField(documentService, "LOAN_PAYMENT_SCHEDULE_TEXT", LOAN_PAYMENT_SCHEDULE_TEXT);

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

        EmploymentDTO employmentDTO = new EmploymentDTO()
                .employmentStatus(SELF_EMPLOYED)
                .employerINN("1234567891")
                .salary(new BigDecimal("60000"))
                .position(EmploymentDTO.PositionEnum.MID_MANAGER)
                .workExperienceTotal(40)
                .workExperienceCurrent(13);

        LoanApplicationRequestDTO request = new LoanApplicationRequestDTO()
                .amount(BigDecimal.valueOf(900000))
                .term(6)
                .firstName("Mikhail")
                .middleName("Alexey")
                .lastName("Deev")
                .birthdate(LocalDate.of(2000, 6, 4))
                .passportSeries("123456")
                .passportNumber("123456");

        ClientDTO clientDTO = new ClientDTO()
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .email(request.getEmail())
                .birthdate(request.getBirthdate())
                .passportIssueDate(LocalDate.of(2010, 12, 2))
                .passportIssueBranch("1234")
                .passportNumber("123456")
                .passportSeries("1234")
                .dependentAmount(2)
                .employment(employmentDTO);

        List<ApplicationStatusHistoryDTO> statusHistoryList = List.of(
                new ApplicationStatusHistoryDTO()
                        .status(PREAPPROVAL)
                        .time(LocalDateTime.now())
                        .changeType(AUTOMATIC)
        );

        CreditDTO creditDTO = new CreditDTO()
                .id(1L)
                .amount(request.getAmount())
                .term(request.getTerm())
                .monthlyPayment(BigDecimal.valueOf(3000))
                .rate(BigDecimal.valueOf(1))
                .psk(BigDecimal.valueOf(1300000))
                .paymentSchedule(paymentScheduleElementList)
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

        applicationDTO = new ApplicationDTO()
                .id(1L)
                .credit(creditDTO)
                .client(clientDTO)
                .status(PREAPPROVAL)
                .sesCode(String.valueOf(1234))
                .creationDate(LocalDateTime.now())
                .statusHistory(statusHistoryList);
    }


    @Test
    public void createAllDocuments() {
        when(dealFeignClient.getApplicationById(anyLong())).thenReturn(applicationDTO);
        documentService.createAllDocuments(1L);
    }

}
