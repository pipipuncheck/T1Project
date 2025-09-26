package org.example.microservices.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ClientProductRequest {

    @NotNull(message = "Client id cannot be empty")
    private Long clientId;
    @NotNull(message = "Product id cannot be empty")
    private Long productId;
    @NotBlank(message = "Status cannot be empty")
    private String status;
    private BigDecimal loanAmount;
}
