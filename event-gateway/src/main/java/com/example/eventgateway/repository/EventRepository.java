package com.example.eventgateway.repository;

import com.example.eventgateway.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    boolean existsByEventId(String eventId);

    Optional<Event> findByEventId(String eventId);
}