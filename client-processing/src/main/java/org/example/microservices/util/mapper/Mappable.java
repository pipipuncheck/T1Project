package org.example.microservices.util.mapper;

import java.util.List;

public interface Mappable<Entity, DTO> {

    Entity toEntity(DTO dto);

    List<Entity> toEntity(List<DTO> dto);

    DTO toDTO(Entity entity);

    List<DTO> toDTO(List<Entity> entity);
}
