package com.example.eventgateway.service;

import com.example.eventgateway.dto.EventRequest;
import com.example.eventgateway.dto.EventResponse;

import java.util.List;

public interface EventService {

    EventResponse processEvent(
            EventRequest request);

    EventResponse getEvent(
            String eventId);

    List<EventResponse> getEventsByAccount(
            String accountId);
}