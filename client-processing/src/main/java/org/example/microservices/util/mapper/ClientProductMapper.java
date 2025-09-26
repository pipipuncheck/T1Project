package org.example.microservices.util.mapper;

import org.example.microservices.model.ClientProduct;
import org.example.microservices.web.dto.ClientProductResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientProductMapper extends Mappable<ClientProduct, ClientProductResponse> {
}
