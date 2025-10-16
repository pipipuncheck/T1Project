package org.example.microservices.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.example.microservices.model.ClientProduct;
import org.example.microservices.model.enums.Key;
import org.example.microservices.model.enums.Status;
import org.example.microservices.repository.ClientProductRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricService {

    private final ClientProductRepository clientProductRepository;
    private final Map<Key, AtomicLong> productGauges = new EnumMap<>(Key.class);

    private static final EnumSet<Key> CLIENT_PRODUCTS =
            EnumSet.of(Key.DC, Key.CC, Key.NS, Key.PENS);

    private static final EnumSet<Key> CREDIT_PRODUCTS =
            EnumSet.of(Key.IPO, Key.PC, Key.AC);

    public MetricService(ClientProductRepository clientProductRepository,
                         MeterRegistry meterRegistry) {
        this.clientProductRepository = clientProductRepository;

        for (Key type : Key.values()) {
            AtomicLong gauge = meterRegistry.gauge(
                    "bank_products_open_total",
                    Tags.of("type", type.name()),
                    new AtomicLong(0)
            );
            productGauges.put(type, gauge);
        }

        productGauges.put(Key.CLIENT_GROUP,
                meterRegistry.gauge("bank_products_open_total", Tags.of("group", "client"), new AtomicLong(0)));
        productGauges.put(Key.CREDIT_GROUP,
                meterRegistry.gauge("bank_products_open_total", Tags.of("group", "credit"), new AtomicLong(0)));

        updateMetrics();
    }

    @Scheduled(fixedRate = 60000)
    public void updateMetrics() {
        List<ClientProduct> products = clientProductRepository.findAll();

        Map<Key, Long> counts = new EnumMap<>(Key.class);
        for (Key type : Key.values()) {
            long count = products.stream()
                    .filter(p -> p.getStatus() == Status.ACTIVE)
                    .filter(p -> p.getProduct().getKey() == type)
                    .count();
            counts.put(type, count);
        }

        counts.forEach((type, count) ->
                Optional.ofNullable(productGauges.get(type))
                        .ifPresent(gauge -> gauge.set(count)));

        long clientCount = products.stream()
                .filter(p -> p.getStatus() == Status.ACTIVE)
                .filter(p -> CLIENT_PRODUCTS.contains(p.getProduct().getKey()))
                .count();

        long creditCount = products.stream()
                .filter(p -> p.getStatus() == Status.ACTIVE)
                .filter(p -> CREDIT_PRODUCTS.contains(p.getProduct().getKey()))
                .count();


        Optional.ofNullable(productGauges.get(Key.CLIENT_GROUP)).ifPresent(g -> g.set(clientCount));
        Optional.ofNullable(productGauges.get(Key.CREDIT_GROUP)).ifPresent(g -> g.set(creditCount));
    }

    public long countClientProducts() {
        return Optional.ofNullable(productGauges.get(Key.CLIENT_GROUP))
                .map(AtomicLong::get)
                .orElse(0L);
    }

    public long countCreditProducts() {
        return Optional.ofNullable(productGauges.get(Key.CREDIT_GROUP))
                .map(AtomicLong::get)
                .orElse(0L);
    }

    public long countByType(Key type) {
        return Optional.ofNullable(productGauges.get(type))
                .map(AtomicLong::get)
                .orElse(0L);
    }
}