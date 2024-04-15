package ru.practicum.event.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.category.entity.Category;
import ru.practicum.event.dto.create.CreateEventDtoRequest;
import ru.practicum.event.dto.response.EventFullDtoResponse;
import ru.practicum.event.dto.response.EventShortDtoResponse;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.EventState;
import ru.practicum.event.location.entity.Location;
import ru.practicum.user.entity.User;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "category", ignore = true)
    Event mapDtoRequestToEvent(CreateEventDtoRequest dto);

    default Event mapDtoRequestToEvent(User initiator, Category category, Location location, CreateEventDtoRequest dto) {
        Event event = mapDtoRequestToEvent(dto);
        event.setState(EventState.PENDING);
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setLocation(location);
        return event;
    }

    EventFullDtoResponse mapEventToFullDtoResponse(Event event);

    default EventFullDtoResponse mapEventToFullDtoResponse(long confirmedRequests, long views, Event event) {
        EventFullDtoResponse dto = mapEventToFullDtoResponse(event);
        dto.setConfirmedRequests(confirmedRequests);
        dto.setViews(views);
        return dto;
    }

    EventShortDtoResponse mapEventToShortDtoResponse(Event event);

    default EventShortDtoResponse mapEventToShortDtoResponse(long confirmedRequests, long views, Event event) {
        EventShortDtoResponse dto = mapEventToShortDtoResponse(event);
        dto.setConfirmedRequests(confirmedRequests);
        dto.setViews(views);
        return dto;
    }
}
