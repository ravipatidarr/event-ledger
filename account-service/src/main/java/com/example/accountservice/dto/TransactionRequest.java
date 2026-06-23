package com.example.accountservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TransactionRequest {

    private String eventId;
    private String type;
    private BigDecimal amount;
    private Instant eventTimestamp;
    private String currency;
}