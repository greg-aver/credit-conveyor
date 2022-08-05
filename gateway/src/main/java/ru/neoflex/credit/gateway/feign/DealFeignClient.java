package ru.neoflex.credit.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.credit.deal.model.ApplicationDTO;
import ru.neoflex.credit.deal.model.ScoringDataDTO;

import java.util.List;

@FeignClient(name = "deal-feign-client", url = "${feign.url.deal}")
public interface DealFeignClient {

    @GetMapping("/admin/application/{applicationId}")
    ApplicationDTO getApplicationById(@PathVariable Long applicationId);

    @GetMapping("/admin/application/all")
    List<ApplicationDTO> getAllApplications();

    @PutMapping("/admin/application/{applicationId}/status")
    void updateApplicationStatusById(@PathVariable Long applicationId, @RequestParam String statusName);

    @PutMapping("/calculate/{applicationId}")
    ResponseEntity<Void> calculateCredit(@PathVariable Long applicationId, @RequestBody ScoringDataDTO scoringData);

    @PostMapping("/document/{applicationId}/send")
    ResponseEntity<Void> send(@PathVariable Long applicationId);

    @PostMapping("/document/{applicationId}/sign")
    ResponseEntity<Void> sign(@PathVariable Long applicationId);

    @PostMapping("/document/{applicationId}/code")
    ResponseEntity<Void> code(@PathVariable Long applicationId, @RequestBody Integer sesCode);
}
