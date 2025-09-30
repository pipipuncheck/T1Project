package org.example.microservices.repository;

import org.example.microservices.model.Account;
import org.example.microservices.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByAccountAndPaymentDateAndExpiredFalse(Account account, LocalDate paymentDate);


    List<Payment> findByAccountAndPayedAtIsNullAndExpiredFalse(Account account);
}

