package org.example.microservices.service;

import lombok.RequiredArgsConstructor;
import org.example.microservices.model.Account;
import org.example.microservices.repository.AccountRepository;
import org.example.microservices.util.mapper.ClientProductRequestToAccountMapper;
import org.example.microservices.web.dto.ClientProductResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final ClientProductRequestToAccountMapper clientProductRequestToAccountMapper;

    public void createAccount(ClientProductResponse clientProductRequest){

        Account account = clientProductRequestToAccountMapper.toEntity(clientProductRequest);
        account.setBalance(BigDecimal.ZERO);
        account.setInterestRate(BigDecimal.ZERO);
        accountRepository.save(account);
    }
}
