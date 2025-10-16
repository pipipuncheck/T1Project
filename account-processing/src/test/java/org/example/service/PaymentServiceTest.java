package org.example.service;

import org.example.microservices.model.Account;
import org.example.microservices.model.Payment;
import org.example.microservices.repository.AccountRepository;
import org.example.microservices.repository.PaymentRepository;
import org.example.microservices.service.PaymentService;
import org.example.microservices.util.exception.EntityNotFoundException;
import org.example.microservices.web.dto.PaymentDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private PaymentService service;

    @Test
    void createPayment_shouldThrowIfAccountNotFound() {
        PaymentDTO dto = new PaymentDTO();
        dto.setAccountId(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.createPayment(dto));
    }

    @Test
    void createPayment_shouldThrowIfNotCreditAccount() {
        PaymentDTO dto = new PaymentDTO();
        dto.setAccountId(1L);
        Account account = new Account();
        account.setIsRecalc(false);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(IllegalArgumentException.class, () -> service.createPayment(dto));
    }

    @Test
    void createPayment_shouldThrowIfAmountNotEqualToDebt() {
        PaymentDTO dto = new PaymentDTO();
        dto.setAccountId(1L);
        dto.setAmount(BigDecimal.valueOf(100));

        Account account = new Account();
        account.setIsRecalc(true);
        account.setBalance(BigDecimal.ZERO);

        Payment p = new Payment();
        p.setAmount(BigDecimal.valueOf(50));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(paymentRepository.findByAccountAndPayedAtIsNullAndExpiredFalse(account)).thenReturn(List.of(p));

        assertThrows(IllegalArgumentException.class, () -> service.createPayment(dto));
    }

    @Test
    void createPayment_shouldSavePaymentIfValid() {
        PaymentDTO dto = new PaymentDTO();
        dto.setAccountId(1L);
        dto.setAmount(BigDecimal.valueOf(100));

        Account account = new Account();
        account.setIsRecalc(true);
        account.setBalance(BigDecimal.ZERO);

        Payment p = new Payment();
        p.setAmount(BigDecimal.valueOf(100));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(paymentRepository.findByAccountAndPayedAtIsNullAndExpiredFalse(account)).thenReturn(List.of(p));

        service.createPayment(dto);

        verify(accountRepository, atLeastOnce()).save(account);
        verify(paymentRepository, atLeastOnce()).save(any(Payment.class));
    }
}
