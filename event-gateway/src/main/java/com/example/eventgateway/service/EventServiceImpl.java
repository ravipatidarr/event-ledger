package com.example.eventgateway.service;

import com.example.eventgateway.client.AccountClient;
import com.example.eventgateway.dto.EventRequest;
import com.example.eventgateway.dto.EventResponse;
import com.example.eventgateway.dto.TransactionRequest;
import com.example.eventgateway.entity.Event;
import com.example.eventgateway.exception.EventNotFoundException;
import com.example.eventgateway.repository.EventRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final AccountClient accountClient;

    @Override
    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "processEventFallback"
    )
    public EventResponse processEvent(EventRequest request) {

        log.info("Processing event {}", request.getEventId());

        Event existingEvent = eventRepository
                .findByEventId(request.getEventId())
                .orElse(null);

        if (existingEvent != null) {

            log.warn(
                    "Duplicate event received: {}",
                    request.getEventId());

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

        log.info(
                "Event persisted successfully: {}",
                savedEvent.getEventId());

        TransactionRequest transactionRequest =
                TransactionRequest.builder()
                        .eventId(request.getEventId())
                        .type(request.getType())
                        .amount(request.getAmount())
                        .currency(request.getCurrency())
                        .eventTimestamp(request.getEventTimestamp())
                        .build();

        log.info(
                "Calling account-service for accountId={}",
                request.getAccountId());

        accountClient.processTransaction(
                request.getAccountId(),
                transactionRequest);

        log.info(
                "Transaction processed successfully for accountId={}",
                request.getAccountId());

        return mapToResponse(savedEvent);
    }

    @Override
    public EventResponse getEvent(String eventId) {

        log.info("Fetching event {}", eventId);

        Event event = eventRepository
                .findByEventId(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException(
                                "Event not found: " + eventId));

        return mapToResponse(event);
    }

    @Override
    public List<EventResponse> getEventsByAccount(
            String accountId) {

        log.info(
                "Fetching events for accountId={}",
                accountId);

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

    private EventResponse processEventFallback(
            EventRequest request,
            Exception ex) {

        log.error(
                "Account service unavailable for eventId={}",
                request.getEventId(),
                ex);

        return EventResponse.builder()
                .eventId(request.getEventId())
                .accountId(request.getAccountId())
                .type(request.getType())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .eventTimestamp(request.getEventTimestamp())
                .status("SERVICE_UNAVAILABLE")
                .build();
    }
}