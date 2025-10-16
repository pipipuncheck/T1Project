package org.example.service;

import org.example.microservices.model.Client;
import org.example.microservices.model.User;
import org.example.microservices.model.enums.Role;
import org.example.microservices.repository.ClientRepository;
import org.example.microservices.repository.UserRepository;
import org.example.microservices.service.ClientBlockService;
import org.example.microservices.util.exception.EntityNotFoundException;
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
class ClientBlockServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientBlockService service;

    @Test
    void blockClient_shouldUpdateUserRole() {
        Client client = new Client();
        User user = new User();
        user.setRole(Role.CURRENT_CLIENT);
        client.setUser(user);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        service.blockClient(1L);

        verify(userRepository).save(user);
        assertEquals(Role.BLOCKED_CLIENT, user.getRole());
    }

    @Test
    void blockClient_shouldThrowIfClientNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.blockClient(99L));
    }
}
