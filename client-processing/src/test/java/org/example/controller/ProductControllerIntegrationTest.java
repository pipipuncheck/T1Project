package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.microservices.service.ProductService;
import org.example.microservices.web.dto.ProductRequest;
import org.example.microservices.web.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getAll_shouldReturnOk() throws Exception {
        Mockito.when(service.getAll()).thenReturn(List.of(new ProductResponse()));

        mockMvc.perform(get("/api/products")
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void create_shouldReturnOk() throws Exception {
        ProductRequest req = new ProductRequest();
        req.setKey("DC");
        req.setName("Debit Card");

        Mockito.when(service.create(Mockito.any())).thenReturn(new ProductResponse());

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .with(user("master").roles("MASTER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldInvokeService() throws Exception {
        mockMvc.perform(delete("/api/products/7")
                        .with(csrf())
                        .with(user("grand").roles("GRAND_EMPLOYEE")))
                .andExpect(status().isOk());

        Mockito.verify(service).delete(7L);
    }
}
