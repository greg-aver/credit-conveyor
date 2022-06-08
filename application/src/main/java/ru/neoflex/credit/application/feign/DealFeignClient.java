package ru.neoflex.credit.application.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;

import java.util.List;

@FeignClient(name = "deal-feign-client", url = "${properties.feign.url}")
public interface DealFeignClient {
    @PostMapping("/deal/application")
    ResponseEntity<List<LoanOfferDTO>> createApplication(@RequestBody LoanApplicationRequestDTO request);

    @PostMapping("/deal/offer")
    ResponseEntity<Void> offer(@RequestBody LoanOfferDTO loanOfferDTO);
}
