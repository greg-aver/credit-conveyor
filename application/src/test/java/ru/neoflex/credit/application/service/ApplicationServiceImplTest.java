package ru.neoflex.credit.application.service;

import org.junit.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.neoflex.credit.application.exception.PreScoringException;
import ru.neoflex.credit.application.feign.DealFeignClient;
import ru.neoflex.credit.application.service.impl.ApplicationServiceImpl;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationServiceImplTest {
    @Mock
    private DealFeignClient dealFeignClient;

    @InjectMocks
    private ApplicationServiceImpl service;

    @Test
    public void preScoring_ok() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LoanApplicationRequestDTO request = new LoanApplicationRequestDTO()
                .lastName("Deev")
                .firstName("Mikhail")
                .middleName("Alexey")
                .amount(BigDecimal.valueOf(100000))
                .term(12)
                .birthdate(LocalDate.of(2001, 2,3))
                .email("mdeev@neoflex.ru")
                .passportSeries("0102")
                .passportNumber("010203");

        Method preScoring = service.getClass().getDeclaredMethod("preScoring", LoanApplicationRequestDTO.class);
        preScoring.setAccessible(true);
        preScoring.invoke(service, request);

    }
    @Test
    public void preScoring_Invalid_Exception() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LoanApplicationRequestDTO request = new LoanApplicationRequestDTO()
                .lastName("w")
                .firstName("w")
                .middleName("w")
                .amount(BigDecimal.valueOf(1))
                .term(1)
                .birthdate(LocalDate.of(2021, 3, 4))
                .email("23")
                .passportSeries("2r")
                .passportNumber("3e");

        Method preScoring = service.getClass().getDeclaredMethod("preScoring", LoanApplicationRequestDTO.class);
        preScoring.setAccessible(true);
        preScoring.invoke(service, request);

        assertThrows(PreScoringException.class, (Executable) preScoring.invoke(service, request));
    }
}
