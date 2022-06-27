package ru.neoflex.credit.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(url = "feign.url.application", name = "application-feign-client")
public class ApplicationFeignClient {
/*    @PostMapping
    ResponseEntity<List<LoanOfferDTO>> createApplication(@RequestBody LoanAppli)*/
}
