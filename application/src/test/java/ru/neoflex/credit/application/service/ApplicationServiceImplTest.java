package ru.neoflex.credit.application.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.neoflex.credit.application.feign.DealFeignClient;
import ru.neoflex.credit.application.service.impl.ApplicationServiceImpl;
import ru.neoflex.credit.application.validator.ApplicationValidator;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ru.neoflex.credit.application.generator.GeneratorUtils.generateLoanApplicationRequestDTO;
import static ru.neoflex.credit.application.generator.GeneratorUtils.generateLoanOfferDTO;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationServiceImplTest {
    @Mock
    private DealFeignClient dealFeignClient;

    @Mock
    private ApplicationValidator validator;

    @InjectMocks
    private ApplicationServiceImpl service;

    private LoanOfferDTO loanOfferDTO;
    private LoanApplicationRequestDTO request;

    @Before
    public void setUp() {
        request = generateLoanApplicationRequestDTO();
        loanOfferDTO = generateLoanOfferDTO(request);
    }

    @Test
    public void offer() {
        service.offer(loanOfferDTO);
        verify(dealFeignClient, times(1)).offer(loanOfferDTO);
    }

    @Test
    public void createApplication() {

    }

}
