package org.example.microservices.config;

import lombok.RequiredArgsConstructor;
import org.example.microservices.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final JwtService jwtService;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            String serviceToken = jwtService.generateServiceToken();
            request.getHeaders().set("Authorization", "Bearer " + serviceToken);
            return execution.execute(request, body);
        });

        return restTemplate;
    }
}