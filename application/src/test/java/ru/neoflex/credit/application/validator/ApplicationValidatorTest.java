package ru.neoflex.credit.application.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import ru.neoflex.credit.application.exception.PreScoringException;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationValidatorTest {
    @InjectMocks
    private ApplicationValidator validator;

    @Test
    public void preScoring_ok() {
        LoanApplicationRequestDTO request = new LoanApplicationRequestDTO()
                .lastName("Deev")
                .firstName("Mikhail")
                .middleName("Alexey")
                .amount(BigDecimal.valueOf(100000))
                .term(12)
                .birthdate(LocalDate.of(2001, 2, 3))
                .email("konveierov@yandex.ru")
                .passportSeries("0102")
                .passportNumber("123456");

        validator.preScoring(request);
    }

    @Test
    public void preScoring_Exception() {
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

        assertThrows(PreScoringException.class, () ->  validator.preScoring(request));
    }
}
