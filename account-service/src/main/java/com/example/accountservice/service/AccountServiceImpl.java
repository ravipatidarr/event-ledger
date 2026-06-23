package com.example.accountservice.service;

import com.example.accountservice.dto.AccountBalanceResponse;
import com.example.accountservice.dto.TransactionRequest;
import com.example.accountservice.dto.TransactionResponse;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.Transaction;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public TransactionResponse processTransaction(
            String accountId,
            TransactionRequest request) {

        if (transactionRepository.existsByEventId(
                request.getEventId())) {

            throw new RuntimeException(
                    "Duplicate event");
        }

        Account account =
                accountRepository.findById(accountId)
                        .orElse(
                                Account.builder()
                                        .accountId(accountId)
                                        .balance(BigDecimal.ZERO)
                                        .currency(
                                                request.getCurrency())
                                        .build());

        BigDecimal updatedBalance =
                "CREDIT".equalsIgnoreCase(
                        request.getType())
                        ? account.getBalance()
                        .add(request.getAmount())
                        : account.getBalance()
                        .subtract(request.getAmount());

        account.setBalance(updatedBalance);

        accountRepository.save(account);

        Transaction transaction =
                Transaction.builder()
                        .eventId(request.getEventId())
                        .accountId(accountId)
                        .type(request.getType())
                        .amount(request.getAmount())
                        .eventTimestamp(
                                request.getEventTimestamp())
                        .build();

        Transaction saved =
                transactionRepository.save(transaction);

        return TransactionResponse.builder()
                .id(saved.getId())
                .eventId(saved.getEventId())
                .accountId(saved.getAccountId())
                .type(saved.getType())
                .amount(saved.getAmount())
                .eventTimestamp(
                        saved.getEventTimestamp())
                .build();
    }

    @Override
    public AccountBalanceResponse getBalance(
            String accountId) {

        Account account =
                accountRepository.findById(accountId)
                        .orElseThrow();

        return AccountBalanceResponse.builder()
                .accountId(account.getAccountId())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .build();
    }

    @Override
    public List<TransactionResponse> getTransactions(
            String accountId) {

        return transactionRepository
                .findByAccountIdOrderByEventTimestampAsc(
                        accountId)
                .stream()
                .map(tx -> TransactionResponse.builder()
                        .id(tx.getId())
                        .eventId(tx.getEventId())
                        .accountId(tx.getAccountId())
                        .type(tx.getType())
                        .amount(tx.getAmount())
                        .eventTimestamp(
                                tx.getEventTimestamp())
                        .build())
                .toList();
    }
}