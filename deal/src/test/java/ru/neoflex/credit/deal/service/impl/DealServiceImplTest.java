package ru.neoflex.credit.deal.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import ru.neoflex.credit.deal.feign.ConveyorFeignClient;
import ru.neoflex.credit.deal.model.*;
import ru.neoflex.credit.deal.repository.ApplicationRepository;
import ru.neoflex.credit.deal.repository.ClientRepository;
import ru.neoflex.credit.deal.repository.CreditRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.neoflex.credit.deal.model.ApplicationStatusEnum.PREAPPROVAL;
import static ru.neoflex.credit.deal.model.ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC;
import static ru.neoflex.credit.deal.model.CreditStatus.CALCULATED;
import static ru.neoflex.credit.deal.model.EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED;
import static ru.neoflex.credit.deal.model.FinishRegistrationRequestDTO.GenderEnum.MALE;
import static ru.neoflex.credit.deal.model.FinishRegistrationRequestDTO.MaritalStatusEnum.MARRIED;

@RunWith(MockitoJUnitRunner.class)
public class DealServiceImplTest {
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private CreditRepository creditRepository;
    @Mock
    private ConveyorFeignClient conveyorFeignClient;

    @InjectMocks
    private DealServiceImpl service;

    @Test
    public void createApplication() {
        LoanApplicationRequestDTO request = new LoanApplicationRequestDTO()
                .amount(BigDecimal.valueOf(900000))
                .term(6)
                .firstName("Mikhail")
                .middleName("Alexey")
                .lastName("Deev")
                .birthdate(LocalDate.of(2000, 6, 4))
                .passportSeries("0808")
                .passportNumber("010203");

        Passport passportClient = new Passport()
                .series(request.getPassportSeries())
                .number(request.getPassportNumber());

        Client client = new Client()
                .setId(1L)
                .setLastName(request.getLastName())
                .setFirstName(request.getFirstName())
                .setMiddleName(request.getMiddleName())
                .setBirthDate(request.getBirthdate())
                .setEmail(request.getEmail())
                .setPassport(passportClient);

        List<ApplicationStatusHistoryDTO> statusHistoryList = List.of(
                new ApplicationStatusHistoryDTO()
                        .status(PREAPPROVAL)
                        .time(LocalDateTime.now())
                        .changeType(AUTOMATIC)
        );

        Application application = new Application()
                .id(1L)
                .client(client)
                .creationDate(LocalDate.now())
                .status(PREAPPROVAL)
                .statusHistory(statusHistoryList);

        client.setApplication(application);

        LoanOfferDTO loanOffer1 = new LoanOfferDTO()
                .applicationId(1L)
                .requestedAmount(request.getAmount())
                .totalAmount(request.getAmount())
                .term(request.getTerm())
                .monthlyPayment(BigDecimal.valueOf(3000))
                .rate(BigDecimal.valueOf(1))
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

        LoanOfferDTO loanOffer2 = new LoanOfferDTO()
                .applicationId(1L)
                .requestedAmount(request.getAmount())
                .totalAmount(request.getAmount())
                .term(request.getTerm())
                .monthlyPayment(BigDecimal.valueOf(3000))
                .rate(BigDecimal.valueOf(2))
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

        LoanOfferDTO loanOffer3 = new LoanOfferDTO()
                .applicationId(1L)
                .requestedAmount(request.getAmount())
                .totalAmount(request.getAmount())
                .term(request.getTerm())
                .monthlyPayment(BigDecimal.valueOf(3000))
                .rate(BigDecimal.valueOf(3))
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

        LoanOfferDTO loanOffer4 = new LoanOfferDTO()
                .applicationId(1L)
                .requestedAmount(request.getAmount())
                .totalAmount(request.getAmount())
                .term(request.getTerm())
                .monthlyPayment(BigDecimal.valueOf(3000))
                .rate(BigDecimal.valueOf(1))
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

        List<LoanOfferDTO> offersListExpect = new ArrayList<>(List.of(loanOffer1, loanOffer2, loanOffer3, loanOffer4));

        when(clientRepository.save(any())).thenReturn(client);
        when(applicationRepository.save(any())).thenReturn(application);
        when(conveyorFeignClient.createOffers(any(LoanApplicationRequestDTO.class))).thenReturn(ResponseEntity.of(Optional.of(offersListExpect)));

        List<LoanOfferDTO> offersListActual = service.createApplication(request);

        assertEquals(offersListExpect.get(0).getRate(), offersListActual.get(0).getRate());
        assertEquals(offersListExpect.get(1).getRate(), offersListActual.get(1).getRate());
        assertEquals(offersListExpect.get(2).getRate(), offersListActual.get(2).getRate());
        assertEquals(offersListExpect.get(3).getRate(), offersListActual.get(3).getRate());
    }

    @Test
    public void offer() {
        LoanApplicationRequestDTO request = new LoanApplicationRequestDTO()
                .amount(BigDecimal.valueOf(900000))
                .term(6)
                .firstName("Mikhail")
                .middleName("Alexey")
                .lastName("Deyev")
                .birthdate(LocalDate.of(2000, 6, 4))
                .passportSeries("0808")
                .passportNumber("010203");

        Passport passportClient = new Passport()
                .series(request.getPassportSeries())
                .number(request.getPassportNumber());
        Client client = new Client()
                .setId(1L)
                .setLastName(request.getLastName())
                .setFirstName(request.getFirstName())
                .setMiddleName(request.getMiddleName())
                .setBirthDate(request.getBirthdate())
                .setEmail(request.getEmail())
                .setPassport(passportClient);

        List<ApplicationStatusHistoryDTO> statusHistoryList = new ArrayList<>();
        statusHistoryList.add(
                new ApplicationStatusHistoryDTO()
                        .status(PREAPPROVAL)
                        .time(LocalDateTime.now())
                        .changeType(AUTOMATIC)
        );
        Application application = new Application()
                .id(1L)
                .client(client)
                .creationDate(LocalDate.now())
                .status(PREAPPROVAL)
                .statusHistory(statusHistoryList);

        LoanOfferDTO loanOffer = new LoanOfferDTO()
                .applicationId(1L)
                .requestedAmount(request.getAmount())
                .totalAmount(request.getAmount())
                .term(request.getTerm())
                .monthlyPayment(BigDecimal.valueOf(3000))
                .rate(BigDecimal.valueOf(1))
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

        when(applicationRepository.getReferenceById(any()))
                .thenReturn(application);

        service.offer(loanOffer);
    }

    @Test
    public void calculateCredit() {

        EmploymentDTO employment = new EmploymentDTO()
                .employmentStatus(SELF_EMPLOYED)
                .employerINN("00085866")
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
                .passportSeries("0808")
                .passportNumber("010203");

        Passport passportClient = new Passport()
                .series(request.getPassportSeries())
                .number(request.getPassportNumber())
                .issueDate(LocalDate.of(2020, 6, 4))
                .issueBranch("Hospitable department of Dagestan");

        Client client = new Client()
                .setId(1L)
                .setLastName(request.getLastName())
                .setFirstName(request.getFirstName())
                .setMiddleName(request.getMiddleName())
                .setBirthDate(request.getBirthdate())
                .setEmail(request.getEmail())
                .setPassport(passportClient);

        FinishRegistrationRequestDTO finishRegistrationRequestDTO = new FinishRegistrationRequestDTO()
                .gender(MALE)
                .maritalStatus(MARRIED)
                .dependentAmount(1)
                .passportIssueDate(passportClient.getIssueDate())
                .passportIssueBranch(passportClient.getIssueBranch())
                .employment(employment);

        List<ApplicationStatusHistoryDTO> statusHistoryList = new ArrayList<>();
        statusHistoryList.add(
                new ApplicationStatusHistoryDTO()
                        .status(PREAPPROVAL)
                        .time(LocalDateTime.now())
                        .changeType(AUTOMATIC));

        LoanOfferDTO loanOffer = new LoanOfferDTO()
                .applicationId(1L)
                .requestedAmount(request.getAmount())
                .totalAmount(request.getAmount())
                .term(request.getTerm())
                .monthlyPayment(BigDecimal.valueOf(3000))
                .rate(BigDecimal.valueOf(1))
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

        Application application = new Application()
                .id(1L)
                .client(client)
                .creationDate(LocalDate.now())
                .status(PREAPPROVAL)
                .statusHistory(statusHistoryList)
                .appliedOffer(loanOffer);

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

        CreditDTO creditDTO = new CreditDTO()
                .amount(request.getAmount())
                .term(request.getTerm())
                .monthlyPayment(BigDecimal.valueOf(3000))
                .rate(BigDecimal.valueOf(1))
                .psk(BigDecimal.valueOf(1300000))
                .paymentSchedule(paymentScheduleElementList)
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

        Credit credit = new Credit()
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


        when(applicationRepository.getReferenceById(anyLong())).thenReturn(application);
        when(creditRepository.save(any(Credit.class))).thenReturn(credit);
        when(conveyorFeignClient.scoring(any(ScoringDataDTO.class))).thenReturn(ResponseEntity.of(Optional.of(creditDTO)));

        service.calculateCredit(1L, finishRegistrationRequestDTO);
    }
}