package org.example.microservices.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateClientId {

    public String generateClientId(Long id, String regionCode, String branchCode){
        return String.format("%s%s%08d", regionCode, branchCode, id);
    }
}
