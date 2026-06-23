package com.example.accountservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class TransactionResponse {

    private Long id;
    private String eventId;
    private String accountId;
    private String type;
    private BigDecimal amount;
    private Instant eventTimestamp;
}