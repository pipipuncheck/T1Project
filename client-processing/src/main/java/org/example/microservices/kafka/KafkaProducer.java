package org.example.microservices.kafka;


import lombok.RequiredArgsConstructor;
import org.example.microservices.web.dto.ClientProductResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, ClientProductResponse> kafkaTemplate;

    public void sendMessage(String topic, ClientProductResponse clientProductResponse) {
        kafkaTemplate.send(topic, clientProductResponse);
    }
}

