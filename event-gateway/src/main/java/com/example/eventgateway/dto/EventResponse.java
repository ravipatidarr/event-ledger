package com.example.eventgateway.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventResponse {

    private String eventId;
    private String accountId;
    private String status;
}