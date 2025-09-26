package org.example.microservices.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.microservices.model.enums.Status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long clientId;
    private Long productId;
    private BigDecimal balance;
    private BigDecimal interestRate;
    private Boolean isRecalc = false;
    private Boolean cardExist = false;
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Card> cards = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();
}
