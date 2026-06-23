package com.example.eventgateway.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class TransactionRequest {

    private String eventId;
    private String type;
    private BigDecimal amount;
    private String currency;
    private Instant eventTimestamp;
}