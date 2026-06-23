package com.example.eventgateway.client;

import com.example.eventgateway.dto.TransactionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "account-service",
        url = "${account.service.url}"
)
public interface AccountClient {

    @PostMapping("/accounts/{accountId}/transactions")
    void processTransaction(
            @PathVariable String accountId,
            @RequestBody TransactionRequest request);
}