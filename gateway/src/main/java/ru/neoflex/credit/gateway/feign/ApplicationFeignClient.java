package ru.neoflex.credit.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.neoflex.credit.deal.model.LoanApplicationRequestDTO;
import ru.neoflex.credit.deal.model.LoanOfferDTO;

import java.util.List;

@FeignClient(url = "${feign.url.application}", name = "application-feign-client")
public interface ApplicationFeignClient {

    @PostMapping
    ResponseEntity<List<LoanOfferDTO>> createApplication(@RequestBody LoanApplicationRequestDTO request);


    @PutMapping("/offer")
    ResponseEntity<Void> applyOffer(@RequestBody LoanOfferDTO loanOfferDTO);

}
