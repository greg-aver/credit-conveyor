package ru.neoflex.credit.dossier.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "deal-feign-client", url = "${feign.url.deal}")
public interface DealFeignClient {

}
