package org.example.microservices.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.microservices.service.CardService;
import org.example.microservices.web.dto.CardRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public void create(@RequestBody CardRequest cardRequest){
        cardService.createCard(cardRequest);
    }
}
