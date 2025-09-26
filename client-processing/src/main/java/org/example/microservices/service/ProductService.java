package org.example.microservices.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.microservices.model.Product;
import org.example.microservices.model.enums.Key;
import org.example.microservices.repository.ProductRepository;
import org.example.microservices.util.exception.EntityNotFoundException;
import org.example.microservices.util.mapper.ProductRequestToProductMapper;
import org.example.microservices.util.mapper.ProductMapper;
import org.example.microservices.web.dto.ProductRequest;
import org.example.microservices.web.dto.ProductResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductRequestToProductMapper productRequestToProductMapper;

    public List<ProductResponse> getAll(){

        log.info("Все продукты");
        return productMapper.toDTO(productRepository.findAll());
    }

    public ProductResponse getOne(Long id){

        log.info("Продукт с id={}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        return productMapper.toDTO(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest productRequest){

        log.info("Создание продукта: {}", productRequest);
        Product product = productRequestToProductMapper.toEntity(productRequest);
        product.setCreateDate(LocalDate.now());
        Product savedProduct = productRepository.save(product);
        log.debug("Продукт сохранён: {}", savedProduct);

        String productId = productRequest.getKey() + savedProduct.getId();
        savedProduct.setProductId(productId);

        Product updatedProduct = productRepository.save(savedProduct);
        log.debug("Продукт сохранён повторно: {}", updatedProduct);

        log.info("Создание продукта закончено");
        return productMapper.toDTO(updatedProduct);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest productRequest){

        log.info("Обновление продукта с id = {} : {}", id, productRequest);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        product.setName(productRequest.getName());
        product.setKey(Key.valueOf(productRequest.getKey()));

        product.setProductId(productRequest.getKey() + product.getId());
        Product savedProduct = productRepository.save(product);
        log.debug("Продукт обновлен: {}", savedProduct);

        log.info("Обновление продукта закончено");
        return productMapper.toDTO(savedProduct);
    }

    public void delete(Long id){

        log.info("Удалерние продукта с id={}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        productRepository.delete(product);
        log.info("Удаление продукта закончено");
    }
}
