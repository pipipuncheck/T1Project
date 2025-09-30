package org.example.microservices.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.microservices.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {

    private UUID id;  // Ключ сообщения
    private Long accountId;
    private Long cardId;
    private String type;
    private BigDecimal amount;
    private LocalDateTime timestamp;

    public TransactionType getType() {
        return TransactionType.valueOf(type.toUpperCase());
    }
}
