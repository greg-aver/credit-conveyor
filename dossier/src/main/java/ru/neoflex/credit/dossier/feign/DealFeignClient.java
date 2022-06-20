package ru.neoflex.credit.dossier.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.neoflex.credit.deal.model.ApplicationDTO;

@FeignClient(name = "deal-feign-client", url = "${feign.url.deal}")
public interface DealFeignClient {
    //TODO: add method in ms deal
    @GetMapping("/application/{applicationId}")
    ApplicationDTO getApplicationById(@PathVariable Long applicationId);
}
