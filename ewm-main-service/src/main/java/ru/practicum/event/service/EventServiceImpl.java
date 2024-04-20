package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.category.entity.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.dto.CommentDtoResponse;
import ru.practicum.comment.mapping.CommentMapper;
import ru.practicum.comment.service.CommentService;
import ru.practicum.event.controller.SortType;
import ru.practicum.event.dto.response.EventFullDtoResponse;
import ru.practicum.event.dto.response.EventShortDtoResponse;
import ru.practicum.event.dto.response.UpdateEventParticipationsDtoResponse;
import ru.practicum.event.dto.create.CreateEventDtoRequest;
import ru.practicum.event.dto.update.*;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.EventState;
import ru.practicum.event.location.dto.LocationDto;
import ru.practicum.event.location.entity.Location;
import ru.practicum.event.location.mapping.LocationMapper;
import ru.practicum.event.location.repository.LocationRepository;
import ru.practicum.event.mapping.EventMapper;
import ru.practicum.event.mapping.EventPatchUpdater;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.*;
import ru.practicum.participation.dto.ParticipationRequestDtoResponse;
import ru.practicum.participation.entity.ParticipationRequest;
import ru.practicum.participation.entity.ParticipationRequestStatus;
import ru.practicum.participation.mapping.ParticipationRequestMapper;
import ru.practicum.participation.repository.ParticipationRequestRepository;
import ru.practicum.stats.StatsProxy;
import ru.practicum.user.entity.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final LocationMapper locationMapper;
    private final EventMapper eventMapper;
    private final ParticipationRequestMapper participationRequestMapper;
    private final CommentMapper commentMapper;
    private final EventPatchUpdater eventPatchUpdater;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final StatsProxy statsProxy;

    @Override
    public Collection<Event> findEvents(long userId, int from, int size) {
        User initiator = userService.findUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventRepository.findByInitiator(initiator, pageRequest).getContent();
    }

    @Override
    public Collection<Event> findEvents(List<Long> users, List<EventState> states, List<Long> categoryIds, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, int from, int size) {
        List<User> initiators = fillInitiators(users);
        List<Category> categories = fillCategories(categoryIds);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventRepository.findByInitiatorsAndStatesAndCategoriesAndEventDateBetweenStartAndEnd(
                initiators, states, categories, rangeStart, rangeEnd, pageRequest
        ).getContent();
    }

    private List<User> fillInitiators(List<Long> initiatorIds) {
        List<User> initiators = null;
        if (initiatorIds != null) {
            initiators = initiatorIds.stream().map(userService::findUser).collect(Collectors.toList());
        }
        return initiators;
    }

    private List<Category> fillCategories(List<Long> categoryIds) {
        List<Category> categories = null;
        if (categoryIds != null) {
            categories = categoryIds.stream().map(categoryService::findCategory).collect(Collectors.toList());
        }
        return categories;
    }

    @Override
    public Collection<Event> findEvents(String text, List<Long> categoryIds, Boolean paid, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, boolean onlyAvailable, SortType sortType, int from, int size) {
        if (rangeStart != null && rangeStart.isAfter(rangeEnd)) {
            throw new IncorrectRequestException("The specified date range is incorrect");
        }
        List<Category> categories = fillCategories(categoryIds);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (sortType == SortType.EVENT_DATE) {
            pageRequest = pageRequest.withSort(Sort.by(Sort.Direction.DESC, "eventDate"));
        }
        List<Event> events = eventRepository.findByTextAndCategoriesAndPaidAndEventDateBetweenStartAndEnd(
                text, categories, paid, rangeStart, rangeEnd, pageRequest
        ).getContent();
        if (sortType == SortType.VIEWS) {
            events.sort(Comparator.comparingLong(statsProxy::getViews).reversed());
        }
        return onlyAvailable
                ? events.stream().filter(this::isEventAvailable).collect(Collectors.toList())
                : events;
    }

    private boolean isEventAvailable(Event event) {
        return isEventReachedParticipantLimit(event);
    }

    private boolean isEventReachedParticipantLimit(Event event) {
        return (event.getParticipantLimit() - participationRequestRepository.countByStatusAndEvent(ParticipationRequestStatus.CONFIRMED, event)) <= 0;
    }

    @Override
    public List<Event> findEventsByIdIn(List<Long> ids) {
        return eventRepository.findByIdIn(ids);
    }

    @Override
    public Event findEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));
    }

    @Override
    public Event findEvent(long userId, long eventId) {
        User initiator = userService.findUser(userId);
        return eventRepository.findByIdAndInitiator(eventId, initiator)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));
    }

    @Override
    public Event findPublishedEvent(long eventId) {
        return eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));
    }

    @Override
    public Collection<ParticipationRequest> findParticipations(long userId, long eventId) {
        User initiator = userService.findUser(userId);
        Event event = findEvent(eventId);
        if (event.getInitiator().getId() != initiator.getId()) {
            throw new IncorrectRequestException(String.format("Event with id=%d does not belongs user with id=%d", eventId, userId));
        }
        return participationRequestRepository.findByEvent(event);
    }

    @Override
    public Event createEvent(long userId, CreateEventDtoRequest dto) {
        validateEventCreation(dto);
        User user = userService.findUser(userId);
        Category category = categoryService.findCategory(dto.getCategory());
        LocationDto locationDto = dto.getLocation();
        Location location = locationRepository.save(locationMapper.mapDtoToLocation(locationDto));
        Event event = eventMapper.mapDtoRequestToEvent(user, category, location, dto);
        return eventRepository.save(event);
    }

    private void validateEventCreation(CreateEventDtoRequest dto) {
        if (LocalDateTime.now().plusHours(2L).isAfter(dto.getEventDate())) {
            throw new EventDateIsInvalidException("Field: eventDate. Error: должно содержать дату не менее, чем за 2 часа до мероприятия.");
        }
    }

    @Override
    public Event createCommentAtEvent(long eventId, long userId, CommentDtoRequest dto) {
        Event event = findEvent(eventId);
        commentService.createComment(event, userId, dto);
        return event;
    }

    @Override
    public Event updateEvent(long userId, long eventId, PrivateUpdateEventDtoRequest dto) {
        Event event = findEvent(userId, eventId);
        validateEventUpdate(event, dto);
        Category category = null;
        if (dto.getCategory() != null) {
            category = categoryService.findCategory(dto.getCategory());
        }
        Location location = null;
        if (dto.getLocation() != null) {
            location = locationRepository.save(locationMapper.mapDtoToLocation(dto.getLocation()));
        }
        Event updatedEvent = eventPatchUpdater.updateEvent(category, location, event, dto);
        updateEventState(updatedEvent, dto.getStateAction());
        return eventRepository.save(updatedEvent);
    }

    private void validateEventUpdate(Event event, PrivateUpdateEventDtoRequest dto) {
        LocalDateTime eventDate = dto.getEventDate();
        if (eventDate != null && LocalDateTime.now().plusHours(2L).isAfter(eventDate)) {
            throw new EventDateIsInvalidException("Field: eventDate. Error: должно содержать дату не менее, чем за 2 часа до мероприятия.");
        }
        EventState state = event.getState();
        if (state == EventState.PUBLISHED) {
            throw new AccessDeniedException("Only pending or canceled events can be changed");
        }
    }

    private void updateEventState(Event event, PrivateUpdateEventStateAction stateAction) {
        if (stateAction != null) {
            switch (stateAction) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    return;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
            }
        }
    }

    @Override
    public Event updateEvent(long eventId, AdminUpdateEventDtoRequest dto) {
        Event event = findEvent(eventId);
        validateEventUpdate(event, dto);
        Category category = null;
        if (dto.getCategory() != null) {
            category = categoryService.findCategory(dto.getCategory());
        }
        Location location = null;
        if (dto.getLocation() != null) {
            location = locationRepository.save(locationMapper.mapDtoToLocation(dto.getLocation()));
        }
        Event updatedEvent = eventPatchUpdater.updateEvent(category, location, event, dto);
        updateEventState(updatedEvent, dto.getStateAction());
        return eventRepository.save(updatedEvent);
    }

    private void validateEventUpdate(Event event, AdminUpdateEventDtoRequest dto) {
        LocalDateTime eventDate = dto.getEventDate();
        if (eventDate != null && LocalDateTime.now().plusHours(1L).isAfter(eventDate)) {
            throw new EventDateIsInvalidException("Field: eventDate. Error: должно содержать дату не менее, чем за 1 час до публикации.");
        }
        AdminUpdateEventStateAction stateAction = dto.getStateAction();
        EventState state = event.getState();
        if (stateAction == AdminUpdateEventStateAction.PUBLISH_EVENT && state != EventState.PENDING) {
            throw new AccessDeniedException(String.format("Cannot publish the event because it's not in the right state: %s", state.toString()));
        }
        if (stateAction == AdminUpdateEventStateAction.REJECT_EVENT && event.getState() == EventState.PUBLISHED) {
            throw new AccessDeniedException("Cannot reject the event because it's not in the right state: PUBLISHED");
        }
    }

    private void updateEventState(Event event, AdminUpdateEventStateAction stateAction) {
        if (stateAction != null) {
            switch (stateAction) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    return;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
            }
        }
    }

    @Override
    public UpdateEventParticipationsDtoResponse updateParticipationsStatus(long userId, long eventId, UpdateEventParticipationsDtoRequest dto) {
        Event event = findEvent(userId, eventId);
        if (event.isNotModerated()) {
            return makeUpdateEventParticipationsDtoResponse(Collections.emptyList(), Collections.emptyList());
        }
        int countConfirmedRequests = participationRequestRepository.countByStatusAndEvent(ParticipationRequestStatus.CONFIRMED, event);
        int countRemainingParticipants = event.getParticipantLimit() - countConfirmedRequests;
        if (countRemainingParticipants <= 0) {
            throw new ParticipantLimitExceededException("The participant limit has been reached");
        }
        List<ParticipationRequestDtoResponse> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDtoResponse> rejectedRequests = new ArrayList<>();
        ParticipationRequestStatus updatedStatus = getUpdatedStatus(dto.getStatus());
        Iterator<Long> participationIds = dto.getRequestIds().iterator();
        for (int i = 0; i < countRemainingParticipants && participationIds.hasNext(); i++) {
            ParticipationRequest updatedRequest = updateParticipationStatus(participationIds.next(), updatedStatus);
            switch (updatedStatus) {
                case CONFIRMED:
                    confirmedRequests.add(participationRequestMapper.mapParticipationRequestToDtoResponse(updatedRequest));
                    break;
                case REJECTED:
                    rejectedRequests.add(participationRequestMapper.mapParticipationRequestToDtoResponse(updatedRequest));
                    break;
            }
        }
        while (participationIds.hasNext()) {
            ParticipationRequest updatedRequest = updateParticipationStatus(participationIds.next(), ParticipationRequestStatus.REJECTED);
            rejectedRequests.add(participationRequestMapper.mapParticipationRequestToDtoResponse(updatedRequest));
        }
        return makeUpdateEventParticipationsDtoResponse(confirmedRequests, rejectedRequests);
    }

    private ParticipationRequestStatus getUpdatedStatus(UpdateEventParticipationStatus updateStatus) {
        return ParticipationRequestStatus.valueOf(updateStatus.toString());
    }

    private ParticipationRequest updateParticipationStatus(long requestId, ParticipationRequestStatus updatedStatus) {
        ParticipationRequest request = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Request with id=%d was not found", requestId)));
        if (request.getStatus() != ParticipationRequestStatus.PENDING) {
            throw new ParticipationStatusAlreadyDefinedException(String.format("Request's status with id=%d already defined", requestId));
        }
        request.setStatus(updatedStatus);
        return participationRequestRepository.save(request);
    }

    @Override
    public EventFullDtoResponse makeEventFullDtoResponse(Event event) {
        List<CommentDtoResponse> commentDtos = commentMapper.mapCommentsToDtoResponses(commentService.findComments(event));
        long confirmedRequests = participationRequestRepository.countByStatusAndEvent(ParticipationRequestStatus.CONFIRMED, event);
        long views = statsProxy.getViews(event);
        return eventMapper.mapEventToFullDtoResponse(commentDtos, confirmedRequests, views, event);
    }

    @Override
    public EventShortDtoResponse makeEventShortDtoResponse(Event event) {
        long confirmedRequests = participationRequestRepository.countByStatusAndEvent(ParticipationRequestStatus.CONFIRMED, event);
        long views = statsProxy.getViews(event);
        return eventMapper.mapEventToShortDtoResponse(confirmedRequests, views, event);
    }

    @Override
    public UpdateEventParticipationsDtoResponse makeUpdateEventParticipationsDtoResponse(
            List<ParticipationRequestDtoResponse> confirmedRequests,
            List<ParticipationRequestDtoResponse> rejectedRequests) {
        return UpdateEventParticipationsDtoResponse.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }
}
