package org.example.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.example.model.HttpLog;
import org.example.repository.HttpLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class HttpOutcomeRequestAspect {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final HttpLogRepository httpLogRepository;
    private final ObjectMapper objectMapper;

    @Value("${logging.service-name:unknown-service}")
    private String serviceName;

    @AfterReturning(value = "@annotation(org.example.annotation.HttpOutcomeRequestLog)", returning = "result")
    public void logOutcomeRequest(JoinPoint joinPoint, Object result) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("timestamp", LocalDateTime.now().toString());
            message.put("methodSignature", joinPoint.getSignature().toShortString());
            message.put("uri", extractUri(joinPoint.getArgs()));
            message.put("parameters", extractParameters(joinPoint.getArgs()));
            message.put("body", extractBody(joinPoint.getArgs()));
            message.put("direction", "OUTCOME");
            message.put("serviceName", serviceName);

            try {
                String jsonMessage = objectMapper.writeValueAsString(message);

                kafkaTemplate.send(
                        MessageBuilder.withPayload(jsonMessage)
                                .setHeader(KafkaHeaders.TOPIC, "service_logs")
                                .setHeader(KafkaHeaders.KEY, serviceName.getBytes(StandardCharsets.UTF_8))
                                .setHeader("type", "INFO")
                                .build()
                );

                log.info("Outcome HTTP log sent to Kafka");
            } catch (Exception kafkaEx) {
                HttpLog httpLog = HttpLog.builder()
                        .timestamp(LocalDateTime.now())
                        .serviceName(serviceName)
                        .methodSignature(joinPoint.getSignature().toShortString())
                        .uri(extractUri(joinPoint.getArgs()))
                        .parameters(getArgsAsString(joinPoint.getArgs()))
                        .body(extractBody(joinPoint.getArgs()))
                        .direction("OUTCOME")
                        .build();

                httpLogRepository.save(httpLog);
                log.info("Saved outcome HTTP log to DB");
            }

            log.info("[HttpOutcomeRequestLog][{}] Method: {}, URI: {}",
                    serviceName, joinPoint.getSignature().toShortString(), extractUri(joinPoint.getArgs()));

        } catch (Exception e) {
            log.error("Failed to log outcome HTTP request", e);
        }
    }

    private String extractUri(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof String && ((String) arg).startsWith("http")) {
                return (String) arg;
            } else if (arg instanceof URI) {
                return arg.toString();
            }
        }
        return "unknown";
    }

    private Map<String, String> extractParameters(Object[] args) {
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (!(args[i] instanceof HttpEntity)) {
                params.put("arg_" + i, String.valueOf(args[i]));
            }
        }
        return params;
    }

    private String extractBody(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof HttpEntity<?> entity) {
                return String.valueOf(entity.getBody());
            }
        }
        return null;
    }

    private String getArgsAsString(Object[] args) {
        try {
            return objectMapper.writeValueAsString(args);
        } catch (JsonProcessingException e) {
            return java.util.Arrays.toString(args);
        }
    }
}
