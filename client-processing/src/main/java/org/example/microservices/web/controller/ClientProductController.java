package org.example.microservices.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.microservices.service.ClientProductService;
import org.example.microservices.web.dto.ClientProductRequest;
import org.example.microservices.web.dto.ClientProductResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client_products")
@RequiredArgsConstructor
public class ClientProductController {

    private final ClientProductService clientProductService;

    @GetMapping
    public List<ClientProductResponse> getAll(){
        return clientProductService.getAll();
    }

    @GetMapping("/{id}")
    public ClientProductResponse getOne(@PathVariable("id") Long id){
        return clientProductService.getOne(id);
    }

    @PostMapping
    public ClientProductResponse create(@RequestBody ClientProductRequest clientProductRequest){
        return clientProductService.create(clientProductRequest);
    }

    @PutMapping("/{id}")
    public ClientProductResponse update(@PathVariable("id") Long id,
                                  @RequestBody ClientProductRequest clientProductRequest){
        return clientProductService.update(id, clientProductRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        clientProductService.delete(id);
    }
}
