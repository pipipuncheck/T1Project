package org.example.service;

import org.example.microservices.model.Client;
import org.example.microservices.repository.ClientRepository;
import org.example.microservices.service.ClientService;
import org.example.microservices.util.exception.EntityNotFoundException;
import org.example.microservices.util.mapper.ClientToClientInfoMapper;
import org.example.microservices.web.dto.ClientInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository repository;
    @Mock
    private ClientToClientInfoMapper mapper;

    @InjectMocks
    private ClientService service;

    @Test
    void getClientInfo_shouldReturnMappedDto() {
        Client c = new Client();
        when(repository.findById(1L)).thenReturn(Optional.of(c));
        when(mapper.toDTO(c)).thenReturn(new ClientInfo());

        ClientInfo result = service.getClientInfo(1L);

        assertNotNull(result);
        verify(repository).findById(1L);
        verify(mapper).toDTO(c);
    }

    @Test
    void getClientInfo_shouldThrowIfNotFound() {
        when(repository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getClientInfo(5L));
    }
}

