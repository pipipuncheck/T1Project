package org.example.microservices.kafka;

import lombok.RequiredArgsConstructor;
import org.example.microservices.service.AccountService;
import org.example.microservices.service.CardService;
import org.example.microservices.service.PaymentService;
import org.example.microservices.service.TransactionService;
import org.example.microservices.web.dto.CardRequest;
import org.example.microservices.web.dto.ClientProductResponse;
import org.example.microservices.web.dto.PaymentDTO;
import org.example.microservices.web.dto.TransactionDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final CardService cardService;
    private final PaymentService paymentService;

    @KafkaListener(topics = "client_products", groupId = "client-products-group")
    public void clientProductConsumer(ClientProductResponse clientProductRequest) {
        accountService.createAccount(clientProductRequest);
    }

    @KafkaListener(topics = "client_transactions", groupId = "client-transactions-group")
    public void transactionConsumer(TransactionDTO transactionDTO) {
        transactionService.createTransaction(transactionDTO);
    }

    @KafkaListener(topics = "client_cards", groupId = "client-cards-group")
    public void cardConsumer(CardRequest cardRequest) {
        cardService.createCard(cardRequest);
    }

    @KafkaListener(topics = "client_payments", groupId = "client-payments-group")
    public void paymentConsumer(PaymentDTO paymentDTO) {
        paymentService.createPayment(paymentDTO);
    }

}
