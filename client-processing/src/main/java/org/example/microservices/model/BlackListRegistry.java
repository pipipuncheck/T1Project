package org.example.microservices.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.microservices.model.enums.DocumentType;

import java.time.LocalDateTime;

@Entity
@Table(name = "black_list_registry")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlackListRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    private String documentId;
    private LocalDateTime blacklistedAt;
    private String reason;
    private LocalDateTime blacklistExpirationDate;
}
