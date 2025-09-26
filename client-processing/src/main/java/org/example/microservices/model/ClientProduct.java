package org.example.microservices.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.microservices.model.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "client_products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id")
    private Client client;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;
    private LocalDate openDate;
    private LocalDate closeDate;
    @Enumerated(EnumType.STRING)
    private Status status;
    private BigDecimal loanAmount;
}
