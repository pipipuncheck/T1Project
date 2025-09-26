package org.example.microservices.kafka;

import lombok.RequiredArgsConstructor;
import org.example.microservices.service.AccountService;
import org.example.microservices.service.CardService;
import org.example.microservices.service.TransactionService;
import org.example.microservices.web.dto.CardRequest;
import org.example.microservices.web.dto.ClientProductResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final CardService cardService;

    @KafkaListener(topics = "client_products", groupId = "client-products-group")
    public void clientProductConsumer(ClientProductResponse clientProductRequest) {
        accountService.createAccount(clientProductRequest);
    }

    @KafkaListener(topics = "client_transactions", groupId = "client-transactions-group")
    public void transactionConsumer() {
        transactionService.createTransaction();
    }

    @KafkaListener(topics = "client_cards", groupId = "client-cards-group")
    public void cardConsumer(CardRequest cardRequest) {
        cardService.createCard(cardRequest);
    }

}
