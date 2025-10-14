package org.example.microservices.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.microservices.service.ClientBlockService;
import org.example.microservices.service.ClientService;
import org.example.microservices.web.dto.ClientInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientBlockService clientBlockService;

    @GetMapping("/api/clients/{clientId}")
    @PreAuthorize("isAuthenticated()")
    public ClientInfo getClientInfo(@PathVariable Long clientId) {
        return clientService.getClientInfo(clientId);
    }

    @PostMapping("/admin/clients/{clientId}/block")
    @PreAuthorize("hasRole('MASTER') or hasRole('GRAND_EMPLOYEE')")
    public void blockClient(@PathVariable Long clientId) {
        clientBlockService.blockClient(clientId);
    }
}
