package org.example.microservices.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.microservices.model.User;
import org.example.microservices.security.JwtService;
import org.example.microservices.service.AuthService;
import org.example.microservices.web.dto.AuthQuery;
import org.example.microservices.web.dto.ClientRegistrationRequest;
import org.example.microservices.web.dto.JwtQuery;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public JwtQuery registration(@RequestBody ClientRegistrationRequest clientRegistrationRequest){
        User user = authService.registration(clientRegistrationRequest);
        return new JwtQuery(jwtService.generateToken(user));
    }

    @PostMapping("/login")
    public JwtQuery login(@RequestBody AuthQuery authQuery){
        User user = authService.authenticate(authQuery.getUsername(), authQuery.getPassword());
        return new JwtQuery(jwtService.generateToken(user));
    }
}
