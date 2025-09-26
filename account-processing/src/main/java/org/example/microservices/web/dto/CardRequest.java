package org.example.microservices.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardRequest {

    private Long accountId;
    private String cardId;
    private String paymentSystem;
    private String status;
}
