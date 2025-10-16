package org.example.controller;

import org.example.microservices.service.ClientBlockService;
import org.example.microservices.service.ClientService;
import org.example.microservices.web.dto.ClientInfo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @MockitoBean
    private ClientBlockService clientBlockService;

    @Test
    void getClientInfo_shouldReturnOk() throws Exception {
        Mockito.when(clientService.getClientInfo(1L)).thenReturn(new ClientInfo());

        mockMvc.perform(get("/api/clients/1")
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void blockClient_shouldInvokeService() throws Exception {
        mockMvc.perform(post("/admin/clients/5/block")
                        .with(csrf())
                        .with(user("grand").roles("GRAND_EMPLOYEE")))
                .andExpect(status().isOk());

        Mockito.verify(clientBlockService).blockClient(5L);
    }
}
