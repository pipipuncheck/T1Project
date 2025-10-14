package org.example.microservices.service;

import lombok.RequiredArgsConstructor;
import org.example.microservices.model.Client;
import org.example.microservices.repository.ClientRepository;
import org.example.microservices.util.exception.EntityNotFoundException;
import org.example.microservices.util.mapper.ClientToClientInfoMapper;
import org.example.microservices.web.dto.ClientInfo;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientToClientInfoMapper clientToClientInfoMapper;

    public ClientInfo getClientInfo(Long clientId){

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        return clientToClientInfoMapper.toDTO(client);

    }


}
