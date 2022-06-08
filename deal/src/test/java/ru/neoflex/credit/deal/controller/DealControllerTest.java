package ru.neoflex.credit.deal.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.net.SocketFlow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.neoflex.credit.deal.feign.ConveyorFeignClient;
import ru.neoflex.credit.deal.model.LoanOfferDTO;
import ru.neoflex.credit.deal.repository.ApplicationRepository;
import ru.neoflex.credit.deal.repository.ClientRepository;
import ru.neoflex.credit.deal.repository.CreditRepository;
import ru.neoflex.credit.deal.service.impl.DealServiceImpl;

import java.math.BigDecimal;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@WebMvcTest(controllers = DealController.class)
@ComponentScan(basePackages = {"ru/neoflex/credit/deal/service/impl/"})
public class DealControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DealServiceImpl service;
    @MockBean
    private ConveyorFeignClient conveyorFeignClient;
    @MockBean
    private ApplicationRepository applicationRepository;
    @MockBean
    private ClientRepository clientRepository;
    @MockBean
    private CreditRepository creditRepository;

    @Test
    public void offer() throws Exception {
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO()
                .totalAmount(BigDecimal.valueOf(10000))
                .term(12)
                .rate(BigDecimal.valueOf(1.5))
                .monthlyPayment(BigDecimal.valueOf(3300))
                .requestedAmount(BigDecimal.valueOf(3300))
                .isInsuranceEnabled(false)
                .isSalaryClient(false);
/*
        when()
                .thenReturn()*/

        mockMvc.perform(
                        put("/deal/offer")
                                .content(objectMapper.writeValueAsString(loanOfferDTO))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
