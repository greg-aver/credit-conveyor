package ru.neoflex.credit.deal.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import ru.neoflex.credit.deal.feign.ConveyorFeignClient;
import ru.neoflex.credit.deal.model.Client;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;
import ru.neoflex.credit.deal.model.Passport;
import ru.neoflex.credit.deal.repository.ApplicationRepository;
import ru.neoflex.credit.deal.repository.ClientRepository;
import ru.neoflex.credit.deal.repository.CreditRepository;
import ru.neoflex.credit.deal.service.impl.DealServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DealServiceImplTest {
    @Mock
    private ConveyorFeignClient conveyorFeignClient;
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private CreditRepository creditRepository;

    @InjectMocks
    private DealServiceImpl service;

    @Test
    public void offer() {
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(10000))
                .totalAmount(BigDecimal.valueOf(10000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(3000))
                .rate(BigDecimal.valueOf(1.5))
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

/*        when(applicationRepository.getReferenceById(any()))
                .thenReturn()*/
    }

    @Test
    public void createApplication() {
        LoanApplicationRequestDTO request = new LoanApplicationRequestDTO()
                .amount(BigDecimal.valueOf(100000))
                .term(12)
                .firstName("Mikhail")
                .lastName("Deev")
                .middleName("Alexey")
                .email("mdeev@neoflex.ru")
                .birthdate(LocalDate.of(2000, 5, 25))
                .passportSeries("0102")
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

        LoanOfferDTO loanOfferDTO1 = new LoanOfferDTO()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(100000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(3000))
                .rate(BigDecimal.valueOf(1.5))
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

         LoanOfferDTO loanOfferDTO2 = new LoanOfferDTO()
                .applicationId(2L)
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(100000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(3000))
                .rate(BigDecimal.valueOf(1.5))
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

         LoanOfferDTO loanOfferDTO3 = new LoanOfferDTO()
                .applicationId(3L)
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(100000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(3000))
                .rate(BigDecimal.valueOf(1.5))
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

        List<LoanOfferDTO> loanOfferList = List.of(
                loanOfferDTO1, loanOfferDTO2, loanOfferDTO3
        );

        when(clientRepository.save(any()))
                .thenReturn(client);
        when(conveyorFeignClient.createOffers(any()))
                .thenReturn(ResponseEntity.ok(loanOfferList));
        service.createApplication(request);
    }
}
