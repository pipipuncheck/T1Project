package org.example.microservices.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientProductResponse {

    private Long clientId;
    private Long productId;
    private String status;
    private BigDecimal loanAmount;
}
