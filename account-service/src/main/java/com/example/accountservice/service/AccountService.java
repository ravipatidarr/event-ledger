package com.example.accountservice.service;

import com.example.accountservice.dto.AccountBalanceResponse;
import com.example.accountservice.dto.TransactionRequest;
import com.example.accountservice.dto.TransactionResponse;

import java.util.List;

public interface AccountService {

    TransactionResponse processTransaction(
            String accountId,
            TransactionRequest request);

    AccountBalanceResponse getBalance(
            String accountId);

    List<TransactionResponse> getTransactions(
            String accountId);
}