package com.example.eventgateway.service;

import com.example.eventgateway.client.AccountClient;
import com.example.eventgateway.dto.EventRequest;
import com.example.eventgateway.dto.EventResponse;
import com.example.eventgateway.dto.TransactionRequest;
import com.example.eventgateway.entity.Event;
import com.example.eventgateway.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final AccountClient accountClient;

    @Override
    public EventResponse processEvent(EventRequest request) {

        Event existingEvent = eventRepository
                .findByEventId(request.getEventId())
                .orElse(null);

        if (existingEvent != null) {
            return mapToResponse(existingEvent);
        }

        Event event = Event.builder()
                .eventId(request.getEventId())
                .accountId(request.getAccountId())
                .type(request.getType())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .eventTimestamp(request.getEventTimestamp())
                .build();

        Event savedEvent = eventRepository.save(event);

        TransactionRequest transactionRequest =
                TransactionRequest.builder()
                        .eventId(request.getEventId())
                        .type(request.getType())
                        .amount(request.getAmount())
                        .currency(request.getCurrency())
                        .eventTimestamp(request.getEventTimestamp())
                        .build();

        accountClient.processTransaction(
                request.getAccountId(),
                transactionRequest);

        return mapToResponse(savedEvent);
    }

    @Override
    public EventResponse getEvent(String eventId) {

        Event event = eventRepository
                .findByEventId(eventId)
                .orElseThrow(() ->
                        new RuntimeException("Event not found"));

        return mapToResponse(event);
    }

    @Override
    public List<EventResponse> getEventsByAccount(
            String accountId) {

        return eventRepository
                .findByAccountIdOrderByEventTimestampAsc(accountId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private EventResponse mapToResponse(Event event) {

        return EventResponse.builder()
                .eventId(event.getEventId())
                .accountId(event.getAccountId())
                .type(event.getType())
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .eventTimestamp(event.getEventTimestamp())
                .status("SUCCESS")
                .build();
    }
}