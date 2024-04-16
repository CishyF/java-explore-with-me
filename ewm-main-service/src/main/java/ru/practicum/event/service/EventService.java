package ru.practicum.event.service;

import ru.practicum.event.controller.SortType;
import ru.practicum.event.dto.response.EventFullDtoResponse;
import ru.practicum.event.dto.response.EventShortDtoResponse;
import ru.practicum.event.dto.response.UpdateEventParticipationsDtoResponse;
import ru.practicum.event.dto.create.CreateEventDtoRequest;
import ru.practicum.event.dto.update.AdminUpdateEventDtoRequest;
import ru.practicum.event.dto.update.PrivateUpdateEventDtoRequest;
import ru.practicum.event.dto.update.UpdateEventParticipationsDtoRequest;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.EventState;
import ru.practicum.participation.dto.ParticipationRequestDtoResponse;
import ru.practicum.participation.entity.ParticipationRequest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {

    Collection<Event> findEvents(long userId, int from, int size);

    Collection<Event> findEvents(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd, int from, int size);

    Collection<Event> findEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd, boolean onlyAvailable, SortType sort, int from, int size);

    List<Event> findEventsByIdIn(List<Long> ids);

    Event findEvent(long eventId);

    Event findEvent(long userId, long eventId);

    Event findPublishedEvent(long eventId);

    Collection<ParticipationRequest> findParticipations(long userId, long eventId);

    Event createEvent(long userId, CreateEventDtoRequest dto);

    Event updateEvent(long userId, long eventId, PrivateUpdateEventDtoRequest dto);

    Event updateEvent(long eventId, AdminUpdateEventDtoRequest dto);

    UpdateEventParticipationsDtoResponse updateParticipationsStatus(long userId, long eventId,
                                                                    UpdateEventParticipationsDtoRequest dto);

    EventFullDtoResponse makeEventFullDtoResponse(Event event);

    EventShortDtoResponse makeEventShortDtoResponse(Event event);

    UpdateEventParticipationsDtoResponse makeUpdateEventParticipationsDtoResponse(
            List<ParticipationRequestDtoResponse> confirmedRequests,
            List<ParticipationRequestDtoResponse> rejectedRequests);
}
