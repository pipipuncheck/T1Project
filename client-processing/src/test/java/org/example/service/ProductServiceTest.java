package org.example.service;

import org.example.microservices.model.Product;
import org.example.microservices.repository.ProductRepository;
import org.example.microservices.service.ProductService;
import org.example.microservices.util.mapper.ProductMapper;
import org.example.microservices.util.mapper.ProductRequestToProductMapper;
import org.example.microservices.web.dto.ProductRequest;
import org.example.microservices.web.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;
    @Mock
    private ProductMapper mapper;
    @Mock
    private ProductRequestToProductMapper reqMapper;

    @InjectMocks
    private ProductService service;

    @Test
    void getAll_shouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(new Product()));
        when(mapper.toDTO(anyList())).thenReturn(List.of(new ProductResponse()));
        assertEquals(1, service.getAll().size());
    }

    @Test
    void getOne_shouldReturnProductResponse() {
        Product p = new Product();
        when(repository.findById(1L)).thenReturn(Optional.of(p));
        when(mapper.toDTO(p)).thenReturn(new ProductResponse());
        assertNotNull(service.getOne(1L));
    }

    @Test
    void create_shouldSaveProductAndSetId() {
        ProductRequest req = new ProductRequest();
        req.setKey("DC");
        when(reqMapper.toEntity(req)).thenReturn(new Product());
        when(repository.save(any(Product.class))).thenAnswer(i -> {
            Product p = i.getArgument(0);
            p.setId(10L);
            return p;
        });
        when(mapper.toDTO(any(Product.class))).thenReturn(new ProductResponse());

        ProductResponse result = service.create(req);

        assertNotNull(result);
        verify(repository, times(2)).save(any());
    }

    @Test
    void update_shouldModifyAndSaveProduct() {
        ProductRequest req = new ProductRequest();
        req.setName("Updated");
        req.setKey("DC");

        Product existing = new Product();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toDTO(existing)).thenReturn(new ProductResponse());

        ProductResponse result = service.update(1L, req);

        assertNotNull(result);
        verify(repository).save(existing);
    }

    @Test
    void delete_shouldRemoveProduct() {
        Product existing = new Product();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        service.delete(1L);

        verify(repository).delete(existing);
    }
}

