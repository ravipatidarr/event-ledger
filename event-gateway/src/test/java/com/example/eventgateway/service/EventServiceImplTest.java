package com.example.eventgateway.service;

import com.example.eventgateway.client.AccountClient;
import com.example.eventgateway.dto.EventRequest;
import com.example.eventgateway.dto.EventResponse;
import com.example.eventgateway.entity.Event;
import com.example.eventgateway.exception.EventNotFoundException;
import com.example.eventgateway.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private AccountClient accountClient;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void getEvent_success() {

        Event event = Event.builder()
                .eventId("evt-100")
                .accountId("acct-123")
                .type("CREDIT")
                .amount(BigDecimal.valueOf(100))
                .currency("USD")
                .eventTimestamp(Instant.now())
                .build();

        when(eventRepository.findByEventId("evt-100"))
                .thenReturn(Optional.of(event));

        EventResponse response =
                eventService.getEvent("evt-100");

        assertEquals(
                "evt-100",
                response.getEventId());
    }

    @Test
    void getEvent_notFound() {

        when(eventRepository.findByEventId("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                EventNotFoundException.class,
                () -> eventService.getEvent("missing"));
    }

    @Test
    void processEvent_duplicateEvent() {

        Event event = Event.builder()
                .eventId("evt-100")
                .accountId("acct-123")
                .type("CREDIT")
                .amount(BigDecimal.valueOf(100))
                .currency("USD")
                .eventTimestamp(Instant.now())
                .build();

        when(eventRepository.findByEventId("evt-100"))
                .thenReturn(Optional.of(event));

        EventRequest request =
                new EventRequest();

        request.setEventId("evt-100");

        EventResponse response =
                eventService.processEvent(request);

        assertEquals(
                "evt-100",
                response.getEventId());

        verify(accountClient, never())
                .processTransaction(any(), any());
    }
}