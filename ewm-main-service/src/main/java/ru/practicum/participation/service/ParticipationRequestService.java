package ru.practicum.participation.service;

import ru.practicum.participation.entity.ParticipationRequest;

import java.util.Collection;

public interface ParticipationRequestService {

    ParticipationRequest findParticipationRequest(long requestId);

    Collection<ParticipationRequest> findParticipationRequestsByUserId(long userId);

    ParticipationRequest createParticipationRequest(long userId, long eventId);

    ParticipationRequest cancelParticipationRequest(long userId, long requestId);
}
