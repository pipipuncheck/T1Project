package org.example.service;

import org.example.microservices.kafka.KafkaProducer;
import org.example.microservices.model.Client;
import org.example.microservices.model.ClientProduct;
import org.example.microservices.model.Product;
import org.example.microservices.model.enums.Key;
import org.example.microservices.model.enums.Status;
import org.example.microservices.repository.ClientProductRepository;
import org.example.microservices.repository.ClientRepository;
import org.example.microservices.repository.ProductRepository;
import org.example.microservices.service.ClientProductService;
import org.example.microservices.util.exception.EntityNotFoundException;
import org.example.microservices.util.mapper.ClientProductMapper;
import org.example.microservices.web.dto.ClientProductRequest;
import org.example.microservices.web.dto.ClientProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientProductServiceTest {

    @Mock
    private ClientProductRepository clientProductRepository;
    @Mock
    private ClientProductMapper mapper;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private ClientProductService service;

    private Product product;
    private Client client;
    private ClientProductRequest req;
    private ClientProduct entity;

    @BeforeEach
    void setup() {
        product = new Product();
        product.setKey(Key.DC);

        client = new Client();
        client.setId(1L);

        req = new ClientProductRequest();
        req.setProductId(2L);
        req.setClientId(1L);
        req.setStatus("ACTIVE");

        entity = ClientProduct.builder()
                .client(client)
                .product(product)
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    void getAll_shouldReturnMappedList() {
        when(clientProductRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDTO(anyList())).thenReturn(List.of(new ClientProductResponse()));

        List<ClientProductResponse> result = service.getAll();

        assertEquals(1, result.size());
        verify(clientProductRepository).findAll();
    }

    @Test
    void create_shouldSaveAndSendKafka() {
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientProductRepository.save(any())).thenReturn(entity);
        when(mapper.toDTO(any(ClientProduct.class))).thenReturn(new ClientProductResponse());

        ClientProductResponse result = service.create(req);

        assertNotNull(result);
        verify(clientProductRepository).save(any());
        verify(kafkaProducer).sendMessage(anyString(), any());
    }

    @Test
    void create_shouldThrowIfClientNotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        assertThrows(EntityNotFoundException.class, () -> service.create(req));
    }

    @Test
    void update_shouldSaveAndSendKafka() {
        when(clientProductRepository.findById(5L)).thenReturn(Optional.of(entity));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientProductRepository.save(any())).thenReturn(entity);
        when(mapper.toDTO(any(ClientProduct.class))).thenReturn(new ClientProductResponse());

        ClientProductResponse result = service.update(5L, req);

        assertNotNull(result);
        verify(kafkaProducer).sendMessage(anyString(), any());
    }

    @Test
    void delete_shouldRemoveEntity() {
        when(clientProductRepository.findById(1L)).thenReturn(Optional.of(entity));
        service.delete(1L);
        verify(clientProductRepository).delete(entity);
    }
}
