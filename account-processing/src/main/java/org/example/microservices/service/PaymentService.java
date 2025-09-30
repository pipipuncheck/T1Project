package org.example.microservices.service;

import lombok.RequiredArgsConstructor;
import org.example.microservices.model.Account;
import org.example.microservices.model.Payment;
import org.example.microservices.model.enums.PaymentType;
import org.example.microservices.repository.AccountRepository;
import org.example.microservices.repository.PaymentRepository;
import org.example.microservices.util.exception.EntityNotFoundException;
import org.example.microservices.web.dto.PaymentDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;

    public void createPayment(PaymentDTO paymentDTO){

        Account account = accountRepository.findById(paymentDTO.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        if (!Boolean.TRUE.equals(account.getIsRecalc())) {
            throw new IllegalArgumentException("Your account is not credit");
        }

        BigDecimal totalDebt = calculateTotalDebt(account);

        if (paymentDTO.getAmount().compareTo(totalDebt) != 0) {
            throw new IllegalArgumentException("Payment amount doesn't match debt amount");
        }

        account.setBalance(account.getBalance().add(paymentDTO.getAmount()));
        accountRepository.save(account);

        createNewPayment(account, paymentDTO);

        updateExistingPayments(account);

    }

    private BigDecimal calculateTotalDebt(Account account) {
        List<Payment> unpaidPayments = paymentRepository.findByAccountAndPayedAtIsNullAndExpiredFalse(account);

        return unpaidPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void createNewPayment(Account account, PaymentDTO paymentDTO) {

        Payment payment = Payment.builder()
                .account(account)
                .paymentDate(LocalDate.now())
                .amount(paymentDTO.getAmount())
                .isCredit(true)
                .payedAt(LocalDateTime.now())
                .type(PaymentType.DEPOSIT)
                .expired(false)
                .build();

        paymentRepository.save(payment);
    }

    private void updateExistingPayments(Account account) {
        List<Payment> unpaidPayments = paymentRepository.findByAccountAndPayedAtIsNullAndExpiredFalse(account);

        for (Payment payment : unpaidPayments) {
            payment.setPayedAt(LocalDateTime.now());
            payment.setExpired(false);
            paymentRepository.save(payment);
        }

    }
}
