package ru.practicum.event.mapping;

import org.mapstruct.*;
import ru.practicum.category.entity.Category;
import ru.practicum.event.dto.update.AdminUpdateEventDtoRequest;
import ru.practicum.event.dto.update.PrivateUpdateEventDtoRequest;
import ru.practicum.event.entity.Event;
import ru.practicum.event.location.entity.Location;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface EventPatchUpdater {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "state", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event updateEvent(@MappingTarget Event event, PrivateUpdateEventDtoRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "state", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event updateEvent(@MappingTarget Event event, AdminUpdateEventDtoRequest dto);

    default Event updateEvent(Category category, Location location, Event event, PrivateUpdateEventDtoRequest dto) {
        Event updatedEvent = updateEvent(event, dto);
        Optional.ofNullable(category).ifPresent(updatedEvent::setCategory);
        Optional.ofNullable(location).ifPresent(updatedEvent::setLocation);
        return updatedEvent;
    }

    default Event updateEvent(Category category, Location location, Event event, AdminUpdateEventDtoRequest dto) {
        Event updatedEvent = updateEvent(event, dto);
        Optional.ofNullable(category).ifPresent(updatedEvent::setCategory);
        Optional.ofNullable(location).ifPresent(updatedEvent::setLocation);
        return updatedEvent;
    }
}
