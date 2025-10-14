package org.example.microservices.web.dto;

import lombok.*;
import org.w3c.dom.DocumentType;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ClientInfo {

    String firstName;
    String middleName;
    String lastName;
    LocalDate dateOfBirth;
    DocumentType documentType;
    String documentId;
    String documentPrefix;
    String documentSuffix;
}
