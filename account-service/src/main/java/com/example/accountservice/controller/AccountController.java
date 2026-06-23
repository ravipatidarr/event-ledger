package com.example.accountservice.controller;

import com.example.accountservice.dto.AccountBalanceResponse;
import com.example.accountservice.dto.TransactionRequest;
import com.example.accountservice.dto.TransactionResponse;
import com.example.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/{accountId}/transactions")
    public TransactionResponse processTransaction(
            @PathVariable String accountId,
            @RequestBody TransactionRequest request) {

        return accountService
                .processTransaction(
                        accountId,
                        request);
    }

    @GetMapping("/{accountId}/balance")
    public AccountBalanceResponse getBalance(
            @PathVariable String accountId) {

        return accountService
                .getBalance(accountId);
    }

    @GetMapping("/{accountId}/transactions")
    public List<TransactionResponse> getTransactions(
            @PathVariable String accountId) {

        return accountService
                .getTransactions(accountId);
    }
}