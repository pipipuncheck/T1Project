package org.example.microservices.util.mapper;

import org.example.microservices.model.Account;
import org.example.microservices.web.dto.ClientProductResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientProductRequestToAccountMapper extends Mappable<Account, ClientProductResponse> {
}
