package com.example.eventgateway.service;

import com.example.eventgateway.client.AccountClient;
import com.example.eventgateway.dto.EventRequest;
import com.example.eventgateway.dto.EventResponse;
import com.example.eventgateway.dto.TransactionRequest;
import com.example.eventgateway.entity.Event;
import com.example.eventgateway.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final AccountClient accountClient;

    @Override
    public EventResponse processEvent(EventRequest request) {

        // Idempotency check
        if (eventRepository.existsByEventId(request.getEventId())) {

            return EventResponse.builder()
                    .eventId(request.getEventId())
                    .accountId(request.getAccountId())
                    .status("DUPLICATE")
                    .build();
        }

        Event event = Event.builder()
                .eventId(request.getEventId())
                .accountId(request.getAccountId())
                .type(request.getType())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .eventTimestamp(request.getEventTimestamp())
                .build();

        eventRepository.save(event);

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

        return EventResponse.builder()
                .eventId(request.getEventId())
                .accountId(request.getAccountId())
                .status("SUCCESS")
                .build();
    }
}