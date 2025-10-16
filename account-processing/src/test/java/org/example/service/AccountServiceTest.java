package org.example.service;

import org.example.microservices.model.Account;
import org.example.microservices.repository.AccountRepository;
import org.example.microservices.service.AccountService;
import org.example.microservices.util.mapper.ClientProductRequestToAccountMapper;
import org.example.microservices.web.dto.ClientProductResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientProductRequestToAccountMapper mapper;

    @InjectMocks
    private AccountService service;

    @Test
    void createAccount_shouldSaveAccountWithZeroBalanceAndInterestRate() {
        ClientProductResponse request = new ClientProductResponse();
        Account mapped = new Account();

        when(mapper.toEntity(request)).thenReturn(mapped);

        service.createAccount(request);

        verify(mapper).toEntity(request);
        verify(accountRepository).save(mapped);
        assert mapped.getBalance().compareTo(BigDecimal.ZERO) == 0;
        assert mapped.getInterestRate().compareTo(BigDecimal.ZERO) == 0;
    }
}