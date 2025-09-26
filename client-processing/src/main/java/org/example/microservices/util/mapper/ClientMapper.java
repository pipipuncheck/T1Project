package org.example.microservices.util.mapper;

import org.example.microservices.model.Client;
import org.example.microservices.web.dto.ClientRegistrationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper extends Mappable<Client, ClientRegistrationRequest> {
}
