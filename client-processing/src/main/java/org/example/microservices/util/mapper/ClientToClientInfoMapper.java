package org.example.microservices.util.mapper;

import org.example.microservices.model.Client;
import org.example.microservices.web.dto.ClientInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientToClientInfoMapper extends Mappable<Client, ClientInfo> {
}
