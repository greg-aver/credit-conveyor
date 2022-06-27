package ru.neoflex.credit.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "deal-feign-client", url = "feign.url.deal")
public class DealFeignClient {
}
