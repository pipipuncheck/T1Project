package org.example.microservices.util.mapper;

import org.example.microservices.model.Product;
import org.example.microservices.web.dto.ProductRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductRequestToProductMapper extends Mappable<Product, ProductRequest> {
}
