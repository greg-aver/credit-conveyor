package ru.neoflex.credit.application.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.neoflex.credit.application.feign.DealFeignClient;
import ru.neoflex.credit.application.service.impl.ApplicationServiceImpl;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationServiceImplTest {
    @Mock
    private DealFeignClient dealFeignClient;

    @InjectMocks
    private ApplicationServiceImpl service;


}
