package org.example.microservices.web.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientRegistrationRequest {
    @NotBlank(message = "Login cannot be empty")
    @Size(min = 2, max = 15, message = "Login must be between 2 and 15 characters")
    private String login;
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    private String email;
    @NotBlank(message = "First name cannot be empty")
    private String firstName;
    private String middleName;
    @NotBlank(message = "Last name cannot be empty")
    private String lastName;
    @NotNull(message = "Date of birth cannot be empty")
    @Past(message = "You can't be born in the future:)")
    private LocalDate dateOfBirth;
    @NotNull(message = "Document type cannot be empty")
    private String documentType;
    @NotBlank(message = "Document ID cannot be empty")
    private String documentId;
    private String documentPrefix;
    private String documentSuffix;
    @NotBlank(message = "Region code cannot be empty")
    @Pattern(regexp = "\\d{2}", message = "Region code must be 2 digits")
    private String regionCode;
    @NotBlank(message = "Branch code cannot be empty")
    @Pattern(regexp = "\\d{2}", message = "Branch code must be 2 digits")
    private String branchCode;

}
