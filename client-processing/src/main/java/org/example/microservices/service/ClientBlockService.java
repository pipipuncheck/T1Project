package org.example.microservices.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.microservices.model.Client;
import org.example.microservices.model.User;
import org.example.microservices.model.enums.Role;
import org.example.microservices.repository.ClientRepository;
import org.example.microservices.repository.UserRepository;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class ClientBlockService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public void blockClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        User user = client.getUser();
        user.setRole(Role.BLOCKED_CLIENT);
        userRepository.save(user);

        log.info("Клиент {} заблокирован", clientId);
    }


}