package org.example.clientprocessing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.clientprocessing.model.enums.Status;

import java.time.LocalDate;

@Entity
@Table(name = "client_products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
}
