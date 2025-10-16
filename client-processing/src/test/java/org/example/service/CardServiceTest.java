package org.example.service;

import org.example.microservices.kafka.KafkaCardProducer;
import org.example.microservices.service.CardService;
import org.example.microservices.web.dto.CardRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private KafkaCardProducer kafkaProducer;

    @InjectMocks
    private CardService service;

    @Test
    void createCard_shouldSendKafkaMessage() {
        CardRequest req = new CardRequest();

        service.createCard(req);

        verify(kafkaProducer).sendMessage(anyString(), eq(req));
    }
}

