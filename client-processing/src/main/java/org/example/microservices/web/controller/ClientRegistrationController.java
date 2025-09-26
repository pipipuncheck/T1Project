package org.example.microservices.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.microservices.service.AuthService;
import org.example.microservices.web.dto.ClientRegistrationRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class ClientRegistrationController {

    private final AuthService authService;

    @PostMapping
    public void registration(@RequestBody ClientRegistrationRequest clientRegistrationRequest){
        authService.registration(clientRegistrationRequest);
    }
}
