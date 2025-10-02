package org.example.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.model.HttpLog;
import org.example.repository.HttpLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class HttpIncomeRequestAspect {

    private final HttpServletRequest httpServletRequest;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final HttpLogRepository httpLogRepository;
    private final ObjectMapper objectMapper;

    @Value("${logging.service-name:unknown-service}")
    private String serviceName;

    @Before("@annotation(org.example.annotation.HttpIncomeRequestLog)")
    public void logIncomeRequest(JoinPoint joinPoint) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("timestamp", LocalDateTime.now().toString());
            message.put("methodSignature", joinPoint.getSignature().toShortString());
            message.put("uri", extractUri(joinPoint));
            message.put("parameters", extractRequestParameters());
            message.put("body", extractRequestBody(joinPoint.getArgs()));
            message.put("direction", "INCOME");
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

                log.info("Income HTTP log sent to Kafka");
            } catch (Exception kafkaEx) {
                HttpLog httpLog = HttpLog.builder()
                        .timestamp(LocalDateTime.now())
                        .serviceName(serviceName)
                        .methodSignature(joinPoint.getSignature().toShortString())
                        .uri(extractUri(joinPoint))
                        .parameters(getArgsAsString(joinPoint.getArgs()))
                        .body(extractRequestBody(joinPoint.getArgs()))
                        .direction("INCOME")
                        .build();

                httpLogRepository.save(httpLog);
                log.info("Saved income HTTP log to DB");
            }

            log.info("[HttpIncomeRequestLog][{}] Method: {}, URI: {}",
                    serviceName, joinPoint.getSignature().toShortString(), extractUri(joinPoint));

        } catch (Exception e) {
            log.error("Failed to log income HTTP request", e);
        }
    }

    private String extractUri(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        for (Annotation annotation : method.getAnnotations()) {
            if (annotation instanceof RequestMapping mapping) {
                return mapping.value().length > 0 ? mapping.value()[0] : "";
            } else if (annotation instanceof GetMapping mapping) {
                return mapping.value().length > 0 ? mapping.value()[0] : "";
            } else if (annotation instanceof PostMapping mapping) {
                return mapping.value().length > 0 ? mapping.value()[0] : "";
            } else if (annotation instanceof PutMapping mapping) {
                return mapping.value().length > 0 ? mapping.value()[0] : "";
            } else if (annotation instanceof DeleteMapping mapping) {
                return mapping.value().length > 0 ? mapping.value()[0] : "";
            }
        }
        return httpServletRequest.getRequestURI();
    }

    private Map<String, String> extractRequestParameters() {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            params.put(paramName, httpServletRequest.getParameter(paramName));
        }

        return params;
    }

    private String extractRequestBody(Object[] args) {
        for (Object arg : args) {
            if (!isBasicType(arg) && !(arg instanceof HttpServletRequest)) {
                return String.valueOf(arg);
            }
        }
        return null;
    }

    private boolean isBasicType(Object obj) {
        return obj instanceof String || obj instanceof Number ||
                obj instanceof Boolean || obj == null;
    }

    private String getArgsAsString(Object[] args) {
        try {
            return objectMapper.writeValueAsString(args);
        } catch (JsonProcessingException e) {
            return java.util.Arrays.toString(args);
        }
    }
}