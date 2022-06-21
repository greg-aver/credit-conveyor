package ru.neoflex.credit.dossier.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import ru.neoflex.credit.deal.model.ApplicationDTO;
import ru.neoflex.credit.deal.model.ApplicationStatus;

@FeignClient(name = "deal-feign-client", url = "${feign.url.deal}")
public interface DealFeignClient {
    //TODO: add methods in ms deal
    @GetMapping("/application/{applicationId}")
    ApplicationDTO getApplicationById(@PathVariable Long applicationId);

    @PutMapping("/application/{applicationId}/status")
    ApplicationDTO updateApplicationStatusById(@PathVariable Long applicationId, ApplicationStatus applicationStatus);
}
