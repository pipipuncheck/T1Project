package org.example.service;

import org.example.microservices.model.Account;
import org.example.microservices.model.Card;
import org.example.microservices.model.enums.Status;
import org.example.microservices.repository.AccountRepository;
import org.example.microservices.repository.CardRepository;
import org.example.microservices.service.CardService;
import org.example.microservices.util.exception.BlockedAccountException;
import org.example.microservices.util.exception.EntityNotFoundException;
import org.example.microservices.util.mapper.CardRequestToCardMapper;
import org.example.microservices.web.dto.CardRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CardRequestToCardMapper cardMapper;

    @InjectMocks
    private CardService service;

    @Test
    void createCard_shouldThrowIfAccountNotFound() {
        CardRequest request = new CardRequest();
        request.setAccountId(10L);
        when(accountRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.createCard(request));
    }

    @Test
    void createCard_shouldThrowIfAccountBlocked() {
        CardRequest request = new CardRequest();
        request.setAccountId(5L);
        Account blocked = new Account();
        blocked.setStatus(Status.BLOCKED);

        when(accountRepository.findById(5L)).thenReturn(Optional.of(blocked));
        when(cardMapper.toEntity(request)).thenReturn(new Card());

        assertThrows(BlockedAccountException.class, () -> service.createCard(request));
    }

    @Test
    void createCard_shouldSaveCardIfAccountActive() {
        CardRequest request = new CardRequest();
        request.setAccountId(7L);

        Account account = new Account();
        account.setStatus(Status.ACTIVE);

        Card card = new Card();

        when(accountRepository.findById(7L)).thenReturn(Optional.of(account));
        when(cardMapper.toEntity(request)).thenReturn(card);

        service.createCard(request);

        verify(cardRepository).save(card);
        assertEquals(account, card.getAccount());
    }
}
