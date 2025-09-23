package org.example.accountprocessing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.accountprocessing.model.enums.CardStatus;
import org.example.accountprocessing.model.enums.PaymentSystem;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cards")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    private String cardId;
    @Enumerated(EnumType.STRING)
    private PaymentSystem paymentSystem;
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

}
