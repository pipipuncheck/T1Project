package org.example.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.annotation.LogDatasourceError;
import org.example.model.ErrorLog;
import org.example.repository.ErrorLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogDatasourceErrorAspect {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ErrorLogRepository errorLogRepository;
    private final ObjectMapper objectMapper;

    @Value("${logging.service-name:unknown-service}")
    private String serviceName;

    @Around("@annotation(logDatasourceError)")
    public Object logDatasourceError(ProceedingJoinPoint joinPoint,
                                     LogDatasourceError logDatasourceError) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
            Map<String, Object> logMessage = new HashMap<>();
            logMessage.put("timestamp", LocalDateTime.now().toString());
            logMessage.put("methodSignature", joinPoint.getSignature().toShortString());
            logMessage.put("exception", ex.getClass().getName());
            logMessage.put("errorMessage", ex.getMessage());
            logMessage.put("stackTrace", getStackTrace(ex)); // теперь строка
            logMessage.put("methodArguments", getArgsAsString(joinPoint.getArgs()));

            try {
                String jsonMessage = objectMapper.writeValueAsString(logMessage);

                kafkaTemplate.send(
                        MessageBuilder.withPayload(jsonMessage)
                                .setHeader(KafkaHeaders.TOPIC, "service_logs")
                                .setHeader(KafkaHeaders.KEY, serviceName.getBytes(StandardCharsets.UTF_8))
                                .setHeader("type", logDatasourceError.level().name())
                                .build()
                );

                log.info("Error log sent to Kafka topic: service_logs");
            } catch (Exception kafkaEx) {
                ErrorLog errorLog = ErrorLog.builder()
                        .timestamp(LocalDateTime.now())
                        .serviceName(serviceName)
                        .methodSignature(joinPoint.getSignature().toShortString())
                        .stackTrace(getStackTrace(ex))
                        .errorMessage(ex.getMessage())
                        .methodArguments(getArgsAsString(joinPoint.getArgs()))
                        .kafkaError(kafkaEx.getMessage())
                        .build();

                errorLogRepository.save(errorLog);
                log.info("Saved error log to DB, id={}", errorLog.getId());
            }

            log.error("[LogDatasourceError][{}] Method: {}, Error: {}",
                    serviceName, joinPoint.getSignature().toShortString(), ex.getMessage(), ex);

            throw ex;
        }
    }

    private String getStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private String getArgsAsString(Object[] args) {
        try {
            return objectMapper.writeValueAsString(args);
        } catch (JsonProcessingException e) {
            return Arrays.toString(args);
        }
    }
}
