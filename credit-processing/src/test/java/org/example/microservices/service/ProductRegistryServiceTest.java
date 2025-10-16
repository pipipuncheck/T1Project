package org.example.microservices.service;

import org.example.microservices.model.PaymentRegistry;
import org.example.microservices.model.ProductRegistry;
import org.example.microservices.repository.PaymentRegistryRepository;
import org.example.microservices.repository.ProductRegistryRepository;
import org.example.microservices.util.exception.EntityNotFoundException;
import org.example.microservices.web.dto.ClientInfo;
import org.example.microservices.web.dto.ClientProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRegistryServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ProductRegistryRepository productRegistryRepository;

    @Mock
    private PaymentRegistryRepository paymentRegistryRepository;

    @InjectMocks
    private ProductRegistryService service;

    @BeforeEach
    void setUp() throws Exception {
        java.lang.reflect.Field urlField = ProductRegistryService.class.getDeclaredField("ms1ClientInfoUrl");
        urlField.setAccessible(true);
        urlField.set(service, "http://localhost:8080/clients");

        java.lang.reflect.Field limitField = ProductRegistryService.class.getDeclaredField("creditLimit");
        limitField.setAccessible(true);
        limitField.set(service, new BigDecimal("10000"));
    }

    @Test
    void process_shouldThrowIfClientNotFound() {
        ClientProductResponse message = new ClientProductResponse();
        message.setClientId(1L);

        when(restTemplate.getForObject(anyString(), eq(ClientInfo.class))).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> service.process(message));
    }

    @Test
    void process_shouldThrowIfCreditDecisionFails() {
        ClientProductResponse message = new ClientProductResponse();
        message.setClientId(1L);
        message.setLoanAmount(BigDecimal.valueOf(9000));

        when(restTemplate.getForObject(anyString(), eq(ClientInfo.class))).thenReturn(new ClientInfo());
        when(paymentRegistryRepository.findAllPaymentsByClientId(1L))
                .thenReturn(List.of(makePayment(BigDecimal.valueOf(5000))));
        when(paymentRegistryRepository.findOverduePaymentsByClientId(1L))
                .thenReturn(List.of(makePayment(BigDecimal.valueOf(100))));

        assertThrows(IllegalArgumentException.class, () -> service.process(message));
    }

    @Test
    void process_shouldSaveProductAndPaymentsSuccessfully() {
        ClientProductResponse message = new ClientProductResponse();
        message.setClientId(1L);
        message.setProductId(2L);
        message.setLoanAmount(BigDecimal.valueOf(1200));

        ProductRegistry savedRegistry = new ProductRegistry();
        savedRegistry.setId(10L);
        savedRegistry.setOpenDate(LocalDate.now());

        when(restTemplate.getForObject(anyString(), eq(ClientInfo.class))).thenReturn(new ClientInfo());
        when(paymentRegistryRepository.findAllPaymentsByClientId(1L))
                .thenReturn(Collections.emptyList());
        when(paymentRegistryRepository.findOverduePaymentsByClientId(1L))
                .thenReturn(Collections.emptyList());
        when(productRegistryRepository.save(any(ProductRegistry.class))).thenReturn(savedRegistry);

        service.process(message);

        verify(productRegistryRepository).save(any(ProductRegistry.class));
        verify(paymentRegistryRepository).saveAll(anyList());
    }

    @Test
    void checkCreditDecision_shouldReturnFalseIfLimitExceeded() throws Exception {
        ClientProductResponse message = new ClientProductResponse();
        message.setClientId(1L);
        message.setLoanAmount(BigDecimal.valueOf(9000));

        when(paymentRegistryRepository.findAllPaymentsByClientId(1L))
                .thenReturn(List.of(makePayment(BigDecimal.valueOf(2000))));
        when(paymentRegistryRepository.findOverduePaymentsByClientId(1L))
                .thenReturn(Collections.emptyList());

        java.lang.reflect.Method method = ProductRegistryService.class.getDeclaredMethod("checkCreditDecision", ClientProductResponse.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(service, message);
        assertFalse(result);
    }

    @Test
    void generateSchedule_shouldReturn12Payments() throws Exception {
        ProductRegistry registry = new ProductRegistry();
        registry.setOpenDate(LocalDate.now());

        java.lang.reflect.Method method = ProductRegistryService.class.getDeclaredMethod(
                "generateSchedule",
                BigDecimal.class, BigDecimal.class, int.class, ProductRegistry.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<PaymentRegistry> schedule = (List<PaymentRegistry>) method.invoke(
                service,
                new BigDecimal("10000"),
                new BigDecimal("20"),
                12,
                registry);

        assertEquals(12, schedule.size());
        assertNotNull(schedule.getFirst().getPaymentDate());
        assertEquals(0, schedule.get(11).getDebtAmount().compareTo(BigDecimal.ZERO));
    }

    private PaymentRegistry makePayment(BigDecimal amount) {
        PaymentRegistry p = new PaymentRegistry();
        p.setDebtAmount(amount);
        return p;
    }
}