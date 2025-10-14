package org.example.microservices.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.microservices.kafka.KafkaProducer;
import org.example.microservices.model.Client;
import org.example.microservices.model.ClientProduct;
import org.example.microservices.model.Product;
import org.example.microservices.model.enums.Status;
import org.example.microservices.repository.ClientProductRepository;
import org.example.microservices.repository.ClientRepository;
import org.example.microservices.repository.ProductRepository;
import org.example.microservices.util.exception.EntityNotFoundException;
import org.example.microservices.util.mapper.ClientProductMapper;
import org.example.microservices.web.dto.ClientProductRequest;
import org.example.microservices.web.dto.ClientProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientProductService {

    private final ClientProductRepository clientProductRepository;
    private final ClientProductMapper clientProductMapper;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final KafkaProducer kafkaProducer;

    @Value("${app.kafka.topic.client-products}")
    private String clientProductsTopic;
    @Value("${app.kafka.topic.credit-products}")
    private String creditProductsTopic;

    public List<ClientProductResponse> getAll(){

        log.info("Все продукты");
        return clientProductMapper.toDTO(clientProductRepository.findAll());
    }

    public ClientProductResponse getOne(Long id){

        log.info("Продукт с id={}", id);
        ClientProduct clientProduct = clientProductRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client product not found"));

        return clientProductMapper.toDTO(clientProduct);
    }

    @PreAuthorize("hasRole('MASTER')")
    public ClientProductResponse create(ClientProductRequest clientProductRequest){

        log.info("Создание продукта: {}", clientProductRequest);
        Product product = productRepository.findById(clientProductRequest.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        Client client = clientRepository.findById(clientProductRequest.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        ClientProduct clientProduct = ClientProduct.builder()
                .client(client)
                .product(product)
                .openDate(LocalDate.now())
                .status(Status.valueOf(clientProductRequest.getStatus()))
                .build();

        ClientProduct savedClientProduct = clientProductRepository.save(clientProduct);
        log.debug("Продукт сохранён: {}", savedClientProduct);

        ClientProductResponse clientProductResponse = clientProductMapper.toDTO(savedClientProduct);
        clientProductResponse.setProductId(clientProductRequest.getProductId());
        clientProductResponse.setClientId(clientProductRequest.getClientId());
        switch (product.getKey()) {
            case DC, CC, NS, PENS -> kafkaProducer.sendMessage(clientProductsTopic, clientProductResponse);
            case IPO, PC, AC -> kafkaProducer.sendMessage(creditProductsTopic, clientProductResponse);
            }
        log.info("Создание продукта закончено");
        return clientProductResponse;
    }

    @PreAuthorize("hasRole('MASTER') or hasRole('GRAND_EMPLOYEE')")
    public ClientProductResponse update(Long id, ClientProductRequest clientProductRequest){

        log.info("Обновление продукта с id = {} : {}", id, clientProductRequest);
        ClientProduct clientProduct = clientProductRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client product not found"));

        Product product = productRepository.findById(clientProductRequest.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        Client client = clientRepository.findById(clientProductRequest.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        clientProduct.setClient(client);
        clientProduct.setProduct(product);
        clientProduct.setStatus(Status.valueOf(clientProductRequest.getStatus()));
        ClientProduct savedClientProduct = clientProductRepository.save(clientProduct);
        log.debug("Продукт обновлен: {}", savedClientProduct);

        ClientProductResponse clientProductResponse = clientProductMapper.toDTO(savedClientProduct);
        clientProductResponse.setProductId(clientProductRequest.getProductId());
        clientProductResponse.setClientId(clientProductRequest.getClientId());
        switch (product.getKey()) {
            case DC, CC, NS, PENS -> kafkaProducer.sendMessage(clientProductsTopic, clientProductResponse);
            case IPO, PC, AC -> kafkaProducer.sendMessage(creditProductsTopic, clientProductResponse);
        }

        log.info("Обновление продукта закончено");
        return clientProductResponse;
    }


    @PreAuthorize("hasRole('MASTER') or hasRole('GRAND_EMPLOYEE')")
    public void delete(Long id){

        log.info("Удалерние продукта с id={}", id);
        ClientProduct clientProduct = clientProductRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client product not found"));

        clientProductRepository.delete(clientProduct);
        log.info("Удаление продукта закончено");
    }
}
