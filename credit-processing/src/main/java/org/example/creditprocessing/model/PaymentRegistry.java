package org.example.creditprocessing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_registry")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_registry_id", nullable = false)
    private ProductRegistry productRegistry;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private BigDecimal interestRateAmount;
    private BigDecimal debtAmount;
    private Boolean expired;
    private LocalDate paymentExpirationDate;

}

