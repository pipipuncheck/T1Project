package org.example.microservices.util.mapper;

import org.example.microservices.model.User;
import org.example.microservices.web.dto.ClientRegistrationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends Mappable<User, ClientRegistrationRequest> {
}
