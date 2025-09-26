package org.example.microservices.service;

import lombok.RequiredArgsConstructor;
import org.example.microservices.kafka.KafkaCardProducer;
import org.example.microservices.web.dto.CardRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardService {

    private final KafkaCardProducer kafkaProducer;

    @Value("${app.kafka.topic.client-cards}")
    private String topic;

    public void createCard(CardRequest cardRequest){
        kafkaProducer.sendMessage(topic, cardRequest);

    }
}
