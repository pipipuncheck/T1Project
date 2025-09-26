package org.example.microservices.repository;

import org.example.microservices.model.PaymentRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRegistryRepository extends JpaRepository<PaymentRegistry, Long> {
    @Query("SELECT p FROM PaymentRegistry p WHERE p.productRegistry.clientId = :clientId AND p.expired = false")
    List<PaymentRegistry> findAllPaymentsByClientId(@Param("clientId") Long clientId);

    @Query("SELECT p FROM PaymentRegistry p WHERE p.productRegistry.clientId = :clientId AND p.paymentExpirationDate < CURRENT_DATE AND p.expired = false")
    List<PaymentRegistry> findOverduePaymentsByClientId(@Param("clientId") Long clientId);
}
