package com.example.eventgateway.controller;

import com.example.eventgateway.dto.EventRequest;
import com.example.eventgateway.dto.EventResponse;
import com.example.eventgateway.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @PostMapping
    public EventResponse createEvent(
            @Valid @RequestBody EventRequest request) {

        return eventService.processEvent(request);
    }

    @GetMapping("/{eventId}")
    public EventResponse getEvent(
            @PathVariable String eventId) {

        return eventService.getEvent(eventId);
    }

    @GetMapping
    public List<EventResponse> getEvents(
            @RequestParam("account") String accountId) {

        return eventService.getEventsByAccount(accountId);
    }
}