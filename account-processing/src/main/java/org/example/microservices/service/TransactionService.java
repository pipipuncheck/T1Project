package org.example.microservices.service;

import lombok.RequiredArgsConstructor;
import org.example.microservices.model.Account;
import org.example.microservices.model.Payment;
import org.example.microservices.model.Transaction;
import org.example.microservices.model.enums.PaymentType;
import org.example.microservices.model.enums.Status;
import org.example.microservices.model.enums.TransactionStatus;
import org.example.microservices.model.enums.TransactionType;
import org.example.microservices.repository.AccountRepository;
import org.example.microservices.repository.CardRepository;
import org.example.microservices.repository.PaymentRepository;
import org.example.microservices.repository.TransactionRepository;
import org.example.microservices.util.exception.BlockedAccountException;
import org.example.microservices.util.exception.EntityNotFoundException;
import org.example.microservices.web.dto.TransactionDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final PaymentRepository paymentRepository;

    private static final int MAX_TRANSACTIONS = 10;
    private static final Duration TIME_WINDOW = Duration.ofMinutes(5);
    private static final BigDecimal INTEREST_RATE = new BigDecimal("0.15");

    public void createTransaction(TransactionDTO transactionDto){

        Account account = accountRepository.findById(transactionDto.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        if (account.getStatus() == Status.BLOCKED || account.getStatus() == Status.ARRESTED) {
            throw new BlockedAccountException("Account is blocked or arrested");
        }

        if (isTransactionLimitExceeded(transactionDto.getCardId())) {
            blockAccount(account);
            throw new IllegalArgumentException("Transaction limit is exceeded");
        }

        processTransactionAmount(account, transactionDto);

        if (Boolean.TRUE.equals(account.getIsRecalc())) {
            createPaymentSchedule(account, transactionDto);
        }

        if (transactionDto.getType() == TransactionType.DEPOSIT &&
                Boolean.TRUE.equals(account.getIsRecalc())) {
            processScheduledPayments(account);
        }

        accountRepository.save(account);
    }

    private void processScheduledPayments(Account account) {
        LocalDate today = LocalDate.now();

        List<Payment> duePayments = paymentRepository.findByAccountAndPaymentDateAndExpiredFalse(
                account, today);

        for (Payment payment : duePayments) {
            if (account.getBalance().compareTo(payment.getAmount()) >= 0) {
                account.setBalance(account.getBalance().subtract(payment.getAmount()));
                payment.setPayedAt(LocalDateTime.now());
                payment.setType(PaymentType.WITHDRAW);
            } else {
                payment.setExpired(true);
            }
            paymentRepository.save(payment);
        }
    }

    private void createPaymentSchedule(Account account, TransactionDTO transactionDto) {
        if (transactionDto.getType() != TransactionType.DEPOSIT) return;

        BigDecimal monthlyAmount = calculateMonthlyPayment(transactionDto.getAmount());

        for (int i = 1; i <= 12; i++) {
            Payment payment = Payment.builder()
                    .account(account)
                    .paymentDate(LocalDate.now().plusMonths(i))
                    .amount(monthlyAmount)
                    .isCredit(true)
                    .type(PaymentType.WITHDRAW)
                    .payedAt(null)
                    .expired(false)
                    .build();

            paymentRepository.save(payment);
        }
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal principal) {
        BigDecimal monthlyRate = INTEREST_RATE.divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);
        BigDecimal ratePlusOne = BigDecimal.ONE.add(monthlyRate);
        BigDecimal denominator = BigDecimal.ONE.subtract(
                BigDecimal.ONE.divide(ratePlusOne.pow(12), 6, RoundingMode.HALF_UP)
        );

        return principal.multiply(monthlyRate).divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private void processTransactionAmount(Account account, TransactionDTO dto) {
        BigDecimal newBalance;

        if (dto.getType() == TransactionType.DEPOSIT) {
            newBalance = account.getBalance().add(dto.getAmount());
        } else {
            if (account.getBalance().compareTo(dto.getAmount()) >= 0) {
                newBalance = account.getBalance().subtract(dto.getAmount());
            } else {
                throw new IllegalArgumentException("Insufficient funds for account");
            }
        }

        account.setBalance(newBalance);

        Transaction transaction = Transaction.builder()
                .account(account)
                .card(cardRepository.findById(dto.getCardId()).orElse(null))
                .type(dto.getType())
                .amount(dto.getAmount())
                .status(TransactionStatus.COMPLETE)
                .timestamp(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
    }

    private boolean isTransactionLimitExceeded(Long cardId) {
        if (cardId == null) return false;

        LocalDateTime startTime = LocalDateTime.now().minus(TIME_WINDOW);
        int transactionCount = transactionRepository.countByCardIdAndTimestampAfter(
                cardId, startTime);

        return transactionCount >= MAX_TRANSACTIONS;
    }

    private void blockAccount(Account account) {
        account.setStatus(Status.BLOCKED);
        accountRepository.save(account);
    }
}
