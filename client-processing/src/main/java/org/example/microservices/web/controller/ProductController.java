package org.example.microservices.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.microservices.service.ProductService;
import org.example.microservices.web.dto.ProductRequest;
import org.example.microservices.web.dto.ProductResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> getAll(){
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public ProductResponse getOne(@PathVariable("id") Long id){
        return productService.getOne(id);
    }

    @PostMapping
    public ProductResponse create(@RequestBody ProductRequest productRequest){
        return productService.create(productRequest);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable("id") Long id,
                       @RequestBody ProductRequest productRequest){
        return productService.update(id, productRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        productService.delete(id);
    }
}
