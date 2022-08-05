package ru.neoflex.credit.conveyor.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import ru.neoflex.credit.conveyor.exception.ScoringException;
import ru.neoflex.credit.conveyor.model.EmploymentDTO;
import ru.neoflex.credit.conveyor.model.ScoringDataDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(MockitoJUnitRunner.class)
public class ScoringServiceTest {
    @InjectMocks
    private ScoringService validator;

    @Test
    public void scoring_DataValid() {
        String baseRate = "11";
        EmploymentDTO employment = new EmploymentDTO()
                .employmentStatus(EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED)
                .employerINN("1234567891")
                .salary(new BigDecimal("60000"))
                .position(EmploymentDTO.PositionEnum.MID_MANAGER)
                .workExperienceTotal(40)
                .workExperienceCurrent(13);

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO()
                .amount(BigDecimal.valueOf(900000))
                .term(3)
                .firstName("Mikhail")
                .middleName("Alexey")
                .lastName("Deyev")
                .gender(ScoringDataDTO.GenderEnum.MALE)
                .birthdate(LocalDate.of(2000, 6, 4))
                .passportSeries("123456")
                .passportNumber("123456")
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

    @Test
    public void scoring_DataFail() {
        String baseRate = "11";
        EmploymentDTO employment = new EmploymentDTO()
                .employmentStatus(EmploymentDTO.EmploymentStatusEnum.UNEMPLOYED)
                .employerINN("1234567891")
                .salary(new BigDecimal("600"))
                .workExperienceTotal(1)
                .workExperienceCurrent(1);

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO()
                .amount(BigDecimal.valueOf(9000000))
                .term(3)
                .firstName("M")
                .middleName("A")
                .lastName("D")
                .gender(ScoringDataDTO.GenderEnum.MALE)
                .birthdate(LocalDate.of(2015, 6, 4))
                .passportSeries("123456")
                .passportNumber("123456")
                .passportIssueDate(LocalDate.of(2019, 6, 4))
                .passportIssueBranch("Hospitable department of Dagestan")
                .maritalStatus(ScoringDataDTO.MaritalStatusEnum.SINGLE)
                .dependentAmount(900000)
                .employment(employment)
                .account("003456")
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

        assertThrows(ScoringException.class, () -> validator.scoring(scoringDataDTO, baseRate));
    }

}
