package ru.neoflex.credit.deal.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.neoflex.credit.deal.model.CreditDTO;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;
import ru.neoflex.credit.deal.model.ScoringDataDTO;

import java.util.List;

@FeignClient(name = "conveyor-feign", url = "${properties.feign.url}")
public interface ConveyorFeignClient {
    @PostMapping("/offers")
    ResponseEntity<List<LoanOfferDTO>> createOffers(@RequestBody LoanApplicationRequestDTO request);

    @PostMapping("/calculation")
    ResponseEntity<CreditDTO> scoring(@RequestBody ScoringDataDTO scoringDataDTO);
}
