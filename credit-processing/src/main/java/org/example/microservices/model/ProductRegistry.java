package org.example.microservices.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_registry")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long clientId;
    private Long accountId;
    private Long productId;
    private BigDecimal interestRate;
    private LocalDate openDate;
    private Integer monthCount;

    @OneToMany(mappedBy = "productRegistry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentRegistry> payments = new ArrayList<>();
}
