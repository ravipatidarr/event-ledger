package com.example.accountservice.repository;

import com.example.accountservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountIdOrderByEventTimestampAsc(
            String accountId);

    boolean existsByEventId(String eventId);
}