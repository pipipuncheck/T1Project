package org.example.microservices.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.microservices.model.enums.DocumentType;

import java.time.LocalDate;

@Entity
@Table(name = "clients")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String clientId;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    private String documentId;
    private String documentPrefix;
    private String documentSuffix;

}
