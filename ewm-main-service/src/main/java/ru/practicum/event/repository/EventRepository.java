package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.category.entity.Category;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.EventState;
import ru.practicum.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE ((:initiators IS NULL OR e.initiator IN :initiators) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category IN :categories) " +
            "AND (coalesce(:rangeStart, NULL) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (coalesce(:rangeEnd, NULL) IS NULL OR e.eventDate <= :rangeEnd))")
    Page<Event> findByInitiatorsAndStatesAndCategoriesAndEventDateBetweenStartAndEnd(@Param("initiators") List<User> initiators,
                 @Param("states") List<EventState> states, @Param("categories") List<Category> categories,
                 @Param("rangeStart") LocalDateTime rangeStart, @Param("rangeEnd") LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE ((e.state = 1) " +
            "AND (:text IS NULL OR lower(e.annotation) LIKE concat('%', lower(:text), '%') OR lower(e.description) LIKE concat('%', lower(:text), '%')) " +
            "AND (:categories IS NULL OR e.category IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (coalesce(:rangeStart, NULL) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (coalesce(:rangeEnd, NULL) IS NULL OR e.eventDate <= :rangeEnd))")
    Page<Event> findByTextAndCategoriesAndPaidAndEventDateBetweenStartAndEnd(
            @Param("text") String text, @Param("categories") List<Category> categories,
            @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd, Pageable pageable
    );

    Page<Event> findByInitiator(User initiator, Pageable pageable);

    Optional<Event> findByIdAndInitiator(long id, User initiator);

    Optional<Event> findByIdAndState(long id, EventState state);

    @Query(value = "SELECT DISTINCT e.category.name FROM Event e")
    List<String> findUniqueNameCategoriesOfEvents();

    @Query(value = "SELECT e FROM Event e WHERE e.id IN :ids")
    List<Event> findByIdIn(@Param("ids") List<Long> ids);
}
