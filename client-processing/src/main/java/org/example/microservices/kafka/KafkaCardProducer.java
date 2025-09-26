package org.example.microservices.kafka;


import lombok.RequiredArgsConstructor;
import org.example.microservices.web.dto.CardRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCardProducer {

    private final KafkaTemplate<String, CardRequest> kafkaTemplate;

    public void sendMessage(String topic, CardRequest cardRequest) {
        kafkaTemplate.send(topic, cardRequest);
    }
}


