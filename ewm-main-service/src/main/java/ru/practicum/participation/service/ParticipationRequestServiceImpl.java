package ru.practicum.participation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.EventState;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.*;
import ru.practicum.participation.entity.ParticipationRequest;
import ru.practicum.participation.entity.ParticipationRequestStatus;
import ru.practicum.participation.repository.ParticipationRequestRepository;
import ru.practicum.user.entity.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public ParticipationRequest findParticipationRequest(long requestId) {
        return participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Request with id=%d was not found", requestId)));
    }

    @Override
    public Collection<ParticipationRequest> findParticipationRequestsByUserId(long userId) {
        User requester = userService.findUser(userId);
        return participationRequestRepository.findByRequester(requester);
    }

    @Override
    public ParticipationRequest createParticipationRequest(long userId, long eventId) {
        User requester = userService.findUser(userId);
        Event event = eventService.findEvent(eventId);
        validateParticipationRequest(requester, event);
        boolean isEventRequestsAreNotModerated = !event.isRequestModeration() || event.getParticipantLimit() == 0;
        ParticipationRequest participationRequest = makeParticipationRequest(requester, event, isEventRequestsAreNotModerated);
        return participationRequestRepository.save(participationRequest);
    }

    private void validateParticipationRequest(User requester, Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new EventIsNotPublishedException(String.format("Event with id=%d is not published", event.getId()));
        } else if (event.getInitiator().getId() == requester.getId()) {
            throw new ParticipationInOwnEventException(String.format("Initiator can't participate in his own event with id=%d", event.getId()));
        } else if (event.getParticipantLimit() != 0 &&
                event.getParticipantLimit() <= participationRequestRepository.countByStatusAndEvent(ParticipationRequestStatus.CONFIRMED, event)) {
            throw new ParticipantLimitExceededException(String.format("Event with id=%d reached the limit of participants", event.getId()));
        }
    }

    private ParticipationRequest makeParticipationRequest(User requester, Event event, boolean isEventRequestsAreNotModerated) {
        return ParticipationRequest.builder()
                .event(event)
                .requester(requester)
                .created(LocalDateTime.now())
                .status(isEventRequestsAreNotModerated ? ParticipationRequestStatus.CONFIRMED : ParticipationRequestStatus.PENDING)
                .build();
    }

    @Override
    public ParticipationRequest cancelParticipationRequest(long userId, long requestId) {
        ParticipationRequest participationRequest = findParticipationRequest(requestId);
        if (participationRequest.getRequester().getId() != userId) {
            throw new AccessDeniedException(String.format("User with id=%d can't update status of request with id=%d", userId, requestId));
        }
        participationRequest.setStatus(ParticipationRequestStatus.CANCELED);
        return participationRequestRepository.save(participationRequest);
    }
}
