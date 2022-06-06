package ru.neoflex.credit.deal.service.impl;

import ru.neoflex.credit.deal.model.*;
import ru.neoflex.credit.deal.service.abstracts.DealService;

import java.util.List;

public class DealServiceImpl  implements DealService {
    @Override
    public List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO request) {
        return null;
    }

    @Override
    public void offer(LoanOfferDTO loanOfferDTO) {

    }

    @Override
    public void calculateCredit(Long applicationId, ScoringDataDTO scoringDataDTO) {

    }

    private Client createClientByRequest(LoanApplicationRequestDTO request) {
        Passport passportClient = new Passport()
                .series(request.getPassportSeries())
                .number(request.getPassportNumber());

        return new Client()
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .birthDate(request.getBirthdate())
                .email(request.getEmail())
                .passport(passportClient);
    }
}
