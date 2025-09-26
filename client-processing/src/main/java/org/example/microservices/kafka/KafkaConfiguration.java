package org.example.microservices.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    @Bean
    public NewTopic clientProductsTopic(@Value("${app.kafka.topic.client-products}") String topic) {
        return new NewTopic(topic, 1, (short) 1);
    }

    @Bean
    public NewTopic clientCreditProductsTopic(@Value("${app.kafka.topic.credit-products}") String topic) {
        return new NewTopic(topic, 1, (short) 1);
    }

    @Bean
    public NewTopic clientCardTopic(@Value("${app.kafka.topic.client-cards}") String topic) {
        return new NewTopic(topic, 1, (short) 1);
    }
}