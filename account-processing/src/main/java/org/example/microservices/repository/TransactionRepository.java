package org.example.microservices.repository;

import org.example.microservices.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Integer countByCardIdAndTimestampAfter(Long cardId, LocalDateTime timestamp);
}
