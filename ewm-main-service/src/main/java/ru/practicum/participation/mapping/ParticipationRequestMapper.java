package ru.practicum.participation.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.participation.dto.ParticipationRequestDtoResponse;
import ru.practicum.participation.entity.ParticipationRequest;

import java.util.Collection;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {

    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "requester.id", target = "requester")
    ParticipationRequestDtoResponse mapParticipationRequestToDtoResponse(ParticipationRequest participationRequest);

    default Collection<ParticipationRequestDtoResponse> mapParticipationRequestsToDtoResponses(Collection<ParticipationRequest> requests) {
        return requests.stream().map(this::mapParticipationRequestToDtoResponse).collect(Collectors.toList());
    }
}
