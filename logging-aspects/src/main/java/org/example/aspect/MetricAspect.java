package org.example.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class MetricAspect {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${logging.service-name:unknown-service}")
    private String serviceName;

    @Value("${metrics.execution-limit-ms:1000}")
    private long executionLimitMs;

    @Around("@annotation(org.example.annotation.Metric)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;

        if (duration > executionLimitMs) {
            Map<String, Object> message = new HashMap<>();
            message.put("timestamp", LocalDateTime.now().toString());
            message.put("methodSignature", joinPoint.getSignature().toShortString());
            message.put("executionTimeMs", duration);
            message.put("serviceName", serviceName);
            message.put("parameters", getArgsAsString(joinPoint.getArgs()));

            try {
                String json = objectMapper.writeValueAsString(message);
                kafkaTemplate.send(
                        MessageBuilder.withPayload(json)
                                .setHeader(KafkaHeaders.TOPIC, "service_logs")
                                .setHeader(KafkaHeaders.KEY, serviceName.getBytes(StandardCharsets.UTF_8))
                                .setHeader("type", "WARNING")
                                .build()
                );
                log.warn("[Metric][{}] Slow method: {} ({} ms)", serviceName, joinPoint.getSignature().toShortString(), duration);
            } catch (Exception e) {
                log.error("Failed to send metric log to Kafka", e);
            }
        }

        return result;
    }

    private String getArgsAsString(Object[] args) {
        try {
            return objectMapper.writeValueAsString(args);
        } catch (JsonProcessingException e) {
            return Arrays.toString(args);
        }
    }
}
