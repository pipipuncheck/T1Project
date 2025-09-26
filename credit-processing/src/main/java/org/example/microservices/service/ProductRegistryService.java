package org.example.microservices.service;

import lombok.RequiredArgsConstructor;
import org.example.microservices.model.PaymentRegistry;
import org.example.microservices.model.ProductRegistry;
import org.example.microservices.repository.PaymentRegistryRepository;
import org.example.microservices.repository.ProductRegistryRepository;
import org.example.microservices.util.exception.EntityNotFoundException;
import org.example.microservices.web.dto.ClientInfo;
import org.example.microservices.web.dto.ClientProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductRegistryService {

    private final RestTemplate restTemplate;
    private final ProductRegistryRepository productRegistryRepository;
    private final PaymentRegistryRepository paymentRegistryRepository;

    @Value("${ms1.url}")
    private String ms1ClientInfoUrl;

    @Value("${credit.limit}")
    private BigDecimal creditLimit;

    public void process(ClientProductResponse message) {

        ClientInfo client = getClientInfo(message.getClientId());
        if (client == null)
            throw new EntityNotFoundException("Client not found");

        if (!checkCreditDecision(message))
            throw new IllegalArgumentException("Limit has been exceeded or there are overdue payments");

        ProductRegistry registry = new ProductRegistry();
        registry.setClientId(message.getClientId());
        registry.setProductId(message.getProductId());
        registry.setInterestRate(BigDecimal.valueOf(22));
        registry.setOpenDate(LocalDate.now());
        registry.setMonthCount(12);

        ProductRegistry saved = productRegistryRepository.save(registry);

        List<PaymentRegistry> schedule = generateSchedule(message.getLoanAmount(), registry.getInterestRate(), registry.getMonthCount(), saved);
        paymentRegistryRepository.saveAll(schedule);

    }

    private ClientInfo getClientInfo(Long clientId) {
        String url = ms1ClientInfoUrl + "/" + clientId;
        return restTemplate.getForObject(url, ClientInfo.class);
    }

    private boolean checkCreditDecision(ClientProductResponse message) {
        BigDecimal totalDebt = paymentRegistryRepository.findAllPaymentsByClientId(message.getClientId())
                .stream()
                .map(PaymentRegistry::getDebtAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal newAmount = message.getLoanAmount() != null ? message.getLoanAmount() : BigDecimal.ZERO;
        if (totalDebt.add(newAmount).compareTo(creditLimit) > 0) {
            return false;
        }

        boolean hasOverdue = !paymentRegistryRepository.findOverduePaymentsByClientId(message.getClientId()).isEmpty();
        return !hasOverdue;
    }

    private List<PaymentRegistry> generateSchedule(BigDecimal loanAmount,
                                                   BigDecimal annualRate,
                                                   int months,
                                                   ProductRegistry productRegistry) {
        List<PaymentRegistry> schedule = new ArrayList<>();
        LocalDate startDate = productRegistry.getOpenDate();

        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);
        BigDecimal onePlusI = BigDecimal.ONE.add(monthlyRate);
        BigDecimal pow = onePlusI.pow(months);

        BigDecimal annuityCoefficient = monthlyRate.multiply(pow).divide(pow.subtract(BigDecimal.ONE), 10, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = loanAmount.multiply(annuityCoefficient).setScale(2, RoundingMode.HALF_UP);

        BigDecimal remainingDebt = loanAmount;

        for (int i = 1; i <= months; i++) {
            BigDecimal interest = remainingDebt.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principal = monthlyPayment.subtract(interest).setScale(2, RoundingMode.HALF_UP);
            remainingDebt = remainingDebt.subtract(principal).setScale(2, RoundingMode.HALF_UP);

            PaymentRegistry payment = new PaymentRegistry();
            payment.setProductRegistry(productRegistry);
            payment.setPaymentDate(startDate.plusMonths(i));
            payment.setPaymentExpirationDate(startDate.plusMonths(i).plusDays(10));
            payment.setAmount(monthlyPayment);
            payment.setInterestRateAmount(interest);
            payment.setDebtAmount(remainingDebt.max(BigDecimal.ZERO));
            payment.setExpired(false);

            schedule.add(payment);
        }

        return schedule;
    }
}

