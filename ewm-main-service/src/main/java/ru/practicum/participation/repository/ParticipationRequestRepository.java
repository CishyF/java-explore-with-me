package ru.practicum.participation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.entity.Event;
import ru.practicum.participation.entity.ParticipationRequest;
import ru.practicum.participation.entity.ParticipationRequestStatus;
import ru.practicum.user.entity.User;

import java.util.List;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findByRequester(User requester);

    List<ParticipationRequest> findByEvent(Event event);

    int countByStatusAndEvent(ParticipationRequestStatus status, Event event);

    @Query("SELECT pr.id FROM ParticipationRequest pr WHERE pr.status = 1 AND pr.event = :event")
    List<Long> findIdsByEvent(@Param("event") Event event);
}
