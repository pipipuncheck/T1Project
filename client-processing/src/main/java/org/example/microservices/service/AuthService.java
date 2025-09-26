package org.example.microservices.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.microservices.model.Client;
import org.example.microservices.model.User;
import org.example.microservices.repository.BlackListRegistryRepository;
import org.example.microservices.repository.ClientRepository;
import org.example.microservices.repository.UserRepository;
import org.example.microservices.util.CreateClientId;
import org.example.microservices.util.exception.BlackListException;
import org.example.microservices.util.exception.UserAlreadyExistException;
import org.example.microservices.util.mapper.ClientMapper;
import org.example.microservices.util.mapper.UserMapper;
import org.example.microservices.web.dto.ClientRegistrationRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final BlackListRegistryRepository blackListRegistryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CreateClientId clientIdGenerator;
    private final UserMapper userMapper;
    private final ClientMapper clientMapper;
    private final ClientRepository clientRepository;

    @Transactional
    public void registration(ClientRegistrationRequest clientRegistrationRequest){

        log.info("Регистрации пользователя: {}", clientRegistrationRequest.getLogin());

        if(userRepository.findByLogin(clientRegistrationRequest.getLogin()).isPresent())
            throw new UserAlreadyExistException("User with this login is already exists");

        if(blackListRegistryRepository.findByDocumentId(clientRegistrationRequest.getDocumentId()).isPresent())
            throw new BlackListException("User with this document ID is blacklisted");


        User user = userMapper.toEntity(clientRegistrationRequest);
        user.setPassword(passwordEncoder.encode(clientRegistrationRequest.getPassword()));
        User savedUser = userRepository.save(user);
        log.debug("Пользователь сохранён: {}", savedUser);

        String clientId = clientIdGenerator.generateClientId(savedUser.getId(), clientRegistrationRequest.getRegionCode(), clientRegistrationRequest.getBranchCode());

        Client client = clientMapper.toEntity(clientRegistrationRequest);
        client.setClientId(clientId);
        client.setUser(savedUser);
        Client savedClient = clientRepository.save(client);
        log.debug("Клиент сохранён: {}", savedClient);

        log.info("Регистрация завершена успешно");
    }
}
