package ru.neoflex.credit.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.neoflex.credit.deal.feign.ConveyorFeignClient;
import ru.neoflex.credit.deal.model.*;
import ru.neoflex.credit.deal.repository.ApplicationRepository;
import ru.neoflex.credit.deal.repository.ClientRepository;
import ru.neoflex.credit.deal.repository.CreditRepository;
import ru.neoflex.credit.deal.service.impl.DealServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.neoflex.credit.deal.generator.GeneratorUtils.*;
import static ru.neoflex.credit.deal.model.ApplicationStatusEnum.APPROVED;
import static ru.neoflex.credit.deal.model.FinishRegistrationRequestDTO.GenderEnum.MALE;
import static ru.neoflex.credit.deal.model.FinishRegistrationRequestDTO.MaritalStatusEnum.MARRIED;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ComponentScan(basePackages = {"ru.neoflex.credit.deal.service.impl"})
@WebMvcTest(controllers = DealController.class)
public class DealControllerTest {

    @Autowired
    private DealServiceImpl dealService;

    @MockBean
    private ApplicationRepository applicationRepository;

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private CreditRepository creditRepository;

    @MockBean
    private ConveyorFeignClient conveyorFeignClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    private LoanApplicationRequestDTO request;
    private Passport passportClient;
    private Client client;
    private LoanOfferDTO loanOffer;
    private List<ApplicationStatusHistoryDTO> statusHistoryList;
    private long applicationId;
    private Application application;
    private EmploymentDTO employment;
    private CreditDTO creditDTO;
    private Credit credit;

    @Before
    public void setUp() {
        applicationId = 1L;

        request = generateLoanApplicationRequestDTO();

        passportClient = generatePassportSeriesNumber(request);

        client = generateClient(request, passportClient);

        loanOffer = generateLoanOfferDTO(request);

        statusHistoryList = generateStatusHistoryList();

        application = generateApplication(client, statusHistoryList, loanOffer);

        employment = generateEmploymentDTO();

        List<PaymentScheduleElement> paymentScheduleElementList = generatePaymentScheduleElementList();

        creditDTO = generateCreditDTO(request, paymentScheduleElementList);

        credit = generateCredit(creditDTO);

    }

    @Test
    public void calculateCredit() throws Exception {

        FinishRegistrationRequestDTO finishRegistrationRequestDTO = new FinishRegistrationRequestDTO()
                .gender(MALE)
                .maritalStatus(MARRIED)
                .dependentAmount(1)
                .passportIssueDate(passportClient.getIssueDate())
                .passportIssueBranch(passportClient.getIssueBranch())
                .employment(employment);

        when(applicationRepository.getReferenceById(anyLong())).thenReturn(application);
        when(conveyorFeignClient.scoring(any(ScoringDataDTO.class))).thenReturn(ResponseEntity.of(Optional.of(creditDTO)));
        when(creditRepository.save(any(Credit.class))).thenReturn(credit);

        mvc.perform(put("/deal/calculate/" + applicationId)
                        .content(objectMapper.writeValueAsString(finishRegistrationRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void createApplication() throws Exception {

        LoanApplicationRequestDTO loanApplicationRequest = new LoanApplicationRequestDTO()
                .amount(BigDecimal.valueOf(900000))
                .term(6)
                .firstName("Mikhail")
                .middleName("Alexey")
                .lastName("Deev")
                .birthdate(LocalDate.of(2000, 6, 4))
                .passportSeries("0808")
                .passportNumber("010203");

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

        mvc.perform(post("/deal/application")
                        .content(objectMapper.writeValueAsString(loanApplicationRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(offersListExpect.toArray())));

    }

    @Test
    public void offer() throws Exception {

        when(applicationRepository.getReferenceById(any())).thenReturn(application);
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        mvc.perform(put("/deal/offer")
                        .content(objectMapper.writeValueAsString(loanOffer))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        verify(applicationRepository, times(1)).getReferenceById(applicationId);
        verify(applicationRepository, times(1)).save(argThat((Application app) -> app.id().equals(applicationId) && app.status().equals(APPROVED)));
    }
}