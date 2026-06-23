package com.example.eventgateway.service;

import com.example.eventgateway.dto.EventRequest;
import com.example.eventgateway.dto.EventResponse;

public interface EventService {

    EventResponse processEvent(
            EventRequest request);
}
