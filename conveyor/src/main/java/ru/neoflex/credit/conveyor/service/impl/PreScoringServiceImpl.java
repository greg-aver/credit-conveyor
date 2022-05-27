package ru.neoflex.credit.conveyor.service.impl;

import ru.neoflex.credit.conveyor.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.conveyor.model.LoanOfferDTO;
import ru.neoflex.credit.conveyor.service.abstracts.PreScoringService;

import java.util.List;

public class PreScoringServiceImpl implements PreScoringService {

    //sort list by parameter rate ASC
    @Override
    public List<LoanOfferDTO> createListOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return null;
    }


    private LoanOfferDTO createOffer(boolean isInsuranceEnabled, boolean isSalaryClient) {
        return null;
    }
}
