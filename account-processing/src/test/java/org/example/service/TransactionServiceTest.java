package org.example.service;

import org.example.microservices.model.Account;
import org.example.microservices.model.Transaction;
import org.example.microservices.model.enums.Status;
import org.example.microservices.repository.AccountRepository;
import org.example.microservices.repository.CardRepository;
import org.example.microservices.repository.PaymentRepository;
import org.example.microservices.repository.TransactionRepository;
import org.example.microservices.service.TransactionService;
import org.example.microservices.util.exception.BlockedAccountException;
import org.example.microservices.util.exception.EntityNotFoundException;
import org.example.microservices.web.dto.TransactionDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private TransactionService service;

    @Test
    void createTransaction_shouldThrowIfAccountNotFound() {
        TransactionDTO dto = new TransactionDTO();
        dto.setAccountId(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.createTransaction(dto));
    }

    @Test
    void createTransaction_shouldThrowIfAccountBlocked() {
        Account acc = new Account();
        acc.setStatus(Status.BLOCKED);

        TransactionDTO dto = new TransactionDTO();
        dto.setAccountId(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));

        assertThrows(BlockedAccountException.class, () -> service.createTransaction(dto));
    }

    @Test
    void createTransaction_shouldThrowIfTransactionLimitExceeded() {
        Account acc = new Account();
        acc.setStatus(Status.ACTIVE);

        TransactionDTO dto = new TransactionDTO();
        dto.setAccountId(1L);
        dto.setCardId(10L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));
        when(transactionRepository.countByCardIdAndTimestampAfter(eq(10L), any(LocalDateTime.class)))
                .thenReturn(10); // превышен лимит

        assertThrows(IllegalArgumentException.class, () -> service.createTransaction(dto));
        verify(accountRepository).save(acc); // блокировка
    }

    @Test
    void createTransaction_shouldSaveDepositTransaction() {
        Account acc = new Account();
        acc.setStatus(Status.ACTIVE);
        acc.setBalance(BigDecimal.valueOf(100));

        TransactionDTO dto = new TransactionDTO();
        dto.setAccountId(1L);
        dto.setType("DEPOSIT");
        dto.setAmount(BigDecimal.valueOf(50));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));
        when(transactionRepository.countByCardIdAndTimestampAfter(any(), any())).thenReturn(0);

        service.createTransaction(dto);

        verify(transactionRepository).save(any(Transaction.class));
        verify(accountRepository).save(acc);
        assertEquals(BigDecimal.valueOf(150), acc.getBalance());
    }

    @Test
    void createTransaction_shouldThrowIfInsufficientFundsForWithdraw() {
        Account acc = new Account();
        acc.setStatus(Status.ACTIVE);
        acc.setBalance(BigDecimal.valueOf(10));

        TransactionDTO dto = new TransactionDTO();
        dto.setAccountId(1L);
        dto.setType("WITHDRAW");
        dto.setAmount(BigDecimal.valueOf(100));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));
        when(transactionRepository.countByCardIdAndTimestampAfter(any(), any())).thenReturn(0);

        assertThrows(IllegalArgumentException.class, () -> service.createTransaction(dto));
    }
}