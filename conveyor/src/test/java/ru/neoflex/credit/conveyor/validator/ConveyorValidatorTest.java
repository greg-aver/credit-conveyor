package ru.neoflex.credit.conveyor.validator;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import ru.neoflex.credit.conveyor.exception.ScoringException;
import ru.neoflex.credit.conveyor.model.EmploymentDTO;
import ru.neoflex.credit.conveyor.model.ScoringDataDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ConveyorValidatorTest {
    @InjectMocks
    private ConveyorValidator validator;

    @Test
    public void scoring_DataValid() {
        String baseRate = "11";
        EmploymentDTO employment = new EmploymentDTO()
                .employmentStatus(EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED)
                .employerINN("00085866")
                .salary(new BigDecimal("60000"))
                .position(EmploymentDTO.PositionEnum.MID_MANAGER)
                .workExperienceTotal(40)
                .workExperienceCurrent(13);

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO()
                .amount(BigDecimal.valueOf(900000))
                .term(3)
                .firstName("Mikhail")
                .middleName("Alexey")
                .lastName("Deev")
                .gender(ScoringDataDTO.GenderEnum.MALE)
                .birthdate(LocalDate.of(2000, 6, 4))
                .passportSeries("0808")
                .passportNumber("010203")
                .passportIssueDate(LocalDate.of(2015, 6, 4))
                .passportIssueBranch("Hospitable department of Dagestan")
                .maritalStatus(ScoringDataDTO.MaritalStatusEnum.SINGLE)
                .dependentAmount(900000)
                .employment(employment)
                .account("003456")
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

        assertEquals(BigDecimal.valueOf(11), validator.scoring(scoringDataDTO, baseRate));
    }

}