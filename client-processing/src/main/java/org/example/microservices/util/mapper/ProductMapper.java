package org.example.microservices.util.mapper;

import org.example.microservices.model.Product;
import org.example.microservices.web.dto.ProductResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper extends Mappable<Product, ProductResponse> {
}
