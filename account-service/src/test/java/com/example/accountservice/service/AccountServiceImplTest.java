package com.example.accountservice.service;

import com.example.accountservice.dto.AccountBalanceResponse;
import com.example.accountservice.dto.TransactionRequest;
import com.example.accountservice.entity.Account;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void getBalance_success() {

        Account account = Account.builder()
                .accountId("acct-123")
                .balance(BigDecimal.valueOf(500))
                .currency("USD")
                .build();

        when(accountRepository.findById("acct-123"))
                .thenReturn(Optional.of(account));

        AccountBalanceResponse response =
                accountService.getBalance("acct-123");

        assertEquals(
                BigDecimal.valueOf(500),
                response.getBalance());
    }

    @Test
    void duplicateEvent() {

        TransactionRequest request =
                new TransactionRequest();

        request.setEventId("evt-100");
        request.setAmount(BigDecimal.valueOf(100));
        request.setType("CREDIT");
        request.setCurrency("USD");
        request.setEventTimestamp(Instant.now());

        when(transactionRepository.existsByEventId(
                "evt-100"))
                .thenReturn(true);

        assertThrows(
                RuntimeException.class,
                () -> accountService.processTransaction(
                        "acct-123",
                        request));
    }
}