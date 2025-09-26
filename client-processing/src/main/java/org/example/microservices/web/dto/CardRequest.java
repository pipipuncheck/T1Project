package org.example.microservices.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CardRequest {

    @NotNull(message = "Account id cannot be empty")
    private Long accountId;
    @NotBlank(message = "Card id cannot be empty")
    private String cardId;
    @NotBlank(message = "Payment system cannot be empty")
    private String paymentSystem;
    @NotBlank(message = "Status cannot be empty")
    private String status;
}
