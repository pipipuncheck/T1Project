package org.example.microservices.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.w3c.dom.DocumentType;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientInfo {

    String email;
    String firstName;
    String middleName;
    String lastName;
    LocalDate dateOfBirth;
    DocumentType documentType;
    String documentId;
    String documentPrefix;
    String documentSuffix;
}
