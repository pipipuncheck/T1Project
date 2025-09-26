package org.example.microservices.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.microservices.model.enums.Key;

import java.time.LocalDate;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Key key;
    private LocalDate createDate;
    private String productId;
}
