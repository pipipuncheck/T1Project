package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.microservices.service.CardService;
import org.example.microservices.web.dto.CardRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createCard_shouldInvokeServiceAndReturnOk() throws Exception {
        CardRequest request = new CardRequest(
                1L,
                "1234123412341234",
                "VISA",
                "ACTIVE"
        );

        mockMvc.perform(post("/api/cards")
                        .with(csrf())
                        .with(user("master").roles("MASTER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(cardService).createCard(Mockito.any(CardRequest.class));
    }
}
