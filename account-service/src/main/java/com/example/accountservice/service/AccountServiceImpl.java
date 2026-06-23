package com.example.accountservice.service;

import com.example.accountservice.dto.AccountBalanceResponse;
import com.example.accountservice.dto.TransactionRequest;
import com.example.accountservice.dto.TransactionResponse;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.Transaction;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public TransactionResponse processTransaction(
            String accountId,
            TransactionRequest request) {

        log.info(
                "Processing transaction for eventId={} accountId={}",
                request.getEventId(),
                accountId);

        if (transactionRepository.existsByEventId(
                request.getEventId())) {

            log.warn(
                    "Duplicate transaction detected for eventId={}",
                    request.getEventId());

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

        log.info(
                "Updated balance for accountId={} newBalance={}",
                accountId,
                updatedBalance);

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

        log.info(
                "Transaction persisted successfully id={}",
                saved.getId());

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

        log.info(
                "Fetching balance for accountId={}",
                accountId);

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

        log.info(
                "Fetching transactions for accountId={}",
                accountId);

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