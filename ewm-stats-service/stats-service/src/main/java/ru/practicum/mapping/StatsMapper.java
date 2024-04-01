package ru.practicum.mapping;

import org.mapstruct.Mapper;
import ru.practicum.dto.EndpointHitDtoRequest;
import ru.practicum.dto.EndpointHitDtoResponse;
import ru.practicum.entity.EndpointHit;

@Mapper(componentModel = "spring")
public abstract class StatsMapper {

    public abstract EndpointHit mapDtoRequestToEndpointHit(EndpointHitDtoRequest dto);

    public abstract EndpointHitDtoResponse mapEndpointHitToDtoResponse(EndpointHit hit);
}
