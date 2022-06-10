package ru.neoflex.credit.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.neoflex.credit.deal.model.EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED;
import static ru.neoflex.credit.deal.model.FinishRegistrationRequestDTO.GenderEnum.MALE;
import static ru.neoflex.credit.deal.model.FinishRegistrationRequestDTO.MaritalStatusEnum.MARRIED;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc

//TODO: ПОЧЕМУ БЛЯДЬ ПОДЧЕРКИВАЕТ КРАСНЫМ???!
@WebMvcTest(controllers = DealController.class)
@ComponentScan(basePackages = {"ru/neoflex/credit/deal/service/impl/"})
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

    @Test
    public void calculateCredit() throws Exception {

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

        mvc.perform(put("/deal/calculate/1")
                .content(objectMapper.writeValueAsString(finishRegistrationRequestDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void createApplication() {

        LoanApplicationRequestDTO loanApplicationRequest = new LoanApplicationRequestDTO()
                .amount(BigDecimal.valueOf(900000))
                .term(6)
                .firstName("Mikhail")
                .middleName("Alexey")
                .lastName("Deev")
                .birthdate(LocalDate.of(2000, 6, 4))
                .passportSeries("0808")
                .passportNumber("010203");

    }

    //TODO: дописать @MockBean when thenReturn
    //TODO: 415 status Resolved Exception:
    //             Type = org.springframework.web.HttpMediaTypeNotSupportedException
    @Test
    @DataSet(value = {"datasets/DealController/application.yaml"})
    public void offer() throws Exception {

        LoanApplicationRequestDTO request = new LoanApplicationRequestDTO()
                .amount(BigDecimal.valueOf(900000))
                .term(6)
                .firstName("Mikhail")
                .middleName("Alexey")
                .lastName("Deev")
                .birthdate(LocalDate.of(2000, 6, 4))
                .passportSeries("0808")
                .passportNumber("010203");

        LoanOfferDTO loanOffer = new LoanOfferDTO()
                .applicationId(1L)
                .requestedAmount(request.getAmount())
                .totalAmount(request.getAmount())
                .term(request.getTerm())
                .monthlyPayment(BigDecimal.valueOf(3000))
                .rate(BigDecimal.valueOf(1))
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

        mvc.perform(put("/deal/offer")
                .content(objectMapper.writeValueAsString(loanOffer)))
                .andExpect(status().isOk());
    }
}