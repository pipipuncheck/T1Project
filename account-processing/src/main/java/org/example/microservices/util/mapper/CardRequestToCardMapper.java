package org.example.microservices.util.mapper;

import org.example.microservices.model.Card;
import org.example.microservices.web.dto.CardRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardRequestToCardMapper extends Mappable<Card, CardRequest> {
}
