package org.example.microservices.kafka;

import lombok.RequiredArgsConstructor;
import org.example.microservices.service.ProductRegistryService;
import org.example.microservices.web.dto.ClientProductResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductRegistryConsumer {

    private final ProductRegistryService productRegistryService;

    @KafkaListener(topics = "client_credit_products", groupId = "credit-group")
    public void consume(ClientProductResponse message) {
        productRegistryService.process(message);
    }
}

