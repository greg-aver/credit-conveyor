package ru.neoflex.credit.conveyor.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.conveyor.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.conveyor.model.LoanOfferDTO;
import ru.neoflex.credit.conveyor.service.abstracts.PreScoringService;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PreScoringServiceImpl implements PreScoringService {

    //sort list by parameter rate ASC
    @Override
    public List<LoanOfferDTO> createListOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return List.of(
                createOffer(true, true, loanApplicationRequestDTO),
                createOffer(true, false, loanApplicationRequestDTO),
                createOffer(false, true, loanApplicationRequestDTO),
                createOffer(false, false, loanApplicationRequestDTO)
        );
    }


    private LoanOfferDTO createOffer(boolean isInsuranceEnabled, boolean isSalaryClient, LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return null;
    }
}
