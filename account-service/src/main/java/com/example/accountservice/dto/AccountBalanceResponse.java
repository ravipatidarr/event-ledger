package com.example.accountservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountBalanceResponse {

    private String accountId;
    private BigDecimal balance;
    private String currency;
}