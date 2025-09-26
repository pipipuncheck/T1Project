package org.example.microservices.service;

import lombok.RequiredArgsConstructor;
import org.example.microservices.model.Account;
import org.example.microservices.model.Card;
import org.example.microservices.model.enums.Status;
import org.example.microservices.repository.AccountRepository;
import org.example.microservices.repository.CardRepository;
import org.example.microservices.util.exception.BlockedAccountException;
import org.example.microservices.util.exception.EntityNotFoundException;
import org.example.microservices.util.mapper.CardRequestToCardMapper;
import org.example.microservices.web.dto.CardRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final CardRequestToCardMapper cardRequestToCardMapper;

    public void createCard(CardRequest cardRequest){

        Account account = accountRepository.findById(cardRequest.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        Card card = cardRequestToCardMapper.toEntity(cardRequest);

        if(account.getStatus().equals(Status.BLOCKED))
            throw new BlockedAccountException("Account is blocked");

        card.setAccount(account);
        cardRepository.save(card);




    }
}
