package ru.neoflex.credit.conveyor.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.conveyor.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.conveyor.model.LoanOfferDTO;
import ru.neoflex.credit.conveyor.service.abstracts.LoanCalculatorService;
import ru.neoflex.credit.conveyor.service.abstracts.PreScoringService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PreScoringServiceImpl implements PreScoringService {

    private final LoanCalculatorService loanCalculator;
    @Override
    public List<LoanOfferDTO> createListOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        List<LoanOfferDTO> listOffers = new ArrayList<>();

        listOffers.add(createOffer(true, true, loanApplicationRequestDTO));
        listOffers.add(createOffer(true, false, loanApplicationRequestDTO));
        listOffers.add(createOffer(false, true, loanApplicationRequestDTO));
        listOffers.add(createOffer(false, false, loanApplicationRequestDTO));

        listOffers.sort((o1, o2) -> o1.getRate().compareTo(o2.getRate()));
        return listOffers;
    }

    private LoanOfferDTO createOffer(boolean isInsuranceEnabled, boolean isSalaryClient, LoanApplicationRequestDTO loanApplicationRequestDTO) {
        BigDecimal totalAmount = loanCalculator.calculateTotalAmount(loanApplicationRequestDTO.getAmount(), isInsuranceEnabled);
        BigDecimal rate = loanCalculator.calculateRate(isInsuranceEnabled, isSalaryClient);
        BigDecimal monthlyPayment = loanCalculator.calculateMonthlyPayment(totalAmount, loanApplicationRequestDTO.getTerm(), rate);
        return new LoanOfferDTO()
                .requestedAmount(loanApplicationRequestDTO.getAmount())
                .totalAmount(totalAmount)
                .term(loanApplicationRequestDTO.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient);
    }
}
