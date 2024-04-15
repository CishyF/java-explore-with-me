package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.response.EventFullDtoResponse;
import ru.practicum.event.dto.response.EventShortDtoResponse;
import ru.practicum.event.dto.response.UpdateEventParticipationsDtoResponse;
import ru.practicum.event.dto.create.CreateEventDtoRequest;
import ru.practicum.event.dto.update.PrivateUpdateEventDtoRequest;
import ru.practicum.event.dto.update.UpdateEventParticipationsDtoRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.participation.dto.ParticipationRequestDtoResponse;
import ru.practicum.participation.mapping.ParticipationRequestMapper;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final ParticipationRequestMapper participationRequestMapper;
    private final EventService eventService;

    /**
     * @param userId id текущего пользователя
     * @param from количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size количество элементов в наборе
     * Получение событий, добавленных текущим пользователем
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     */
    @GetMapping
    public Collection<EventShortDtoResponse> getEvents(@PathVariable long userId,
                                                       @RequestParam(required = false, defaultValue = "0") int from,
                                                       @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Пришел GET-запрос main-service/users/{userId={}}/events?from={}&size={} без тела", userId, from, size);
        Collection<EventShortDtoResponse> dtos = eventService.findEvents(userId, from, size)
                .stream().map(eventService::makeEventShortDtoResponse).collect(Collectors.toList());
        log.info("Ответ на GET-запрос main-service/users/{userId={}}/events?from={}&size={} с телом={}", userId, from, size, dtos);
        return dtos;
    }

    /**
     * @param userId id текущего пользователя
     * @param eventId id события
     * Получение полной информации о событии добавленном текущим пользователем
     * В случае, если события с заданным id не найдено, возвращает статус код 404
     */
    @GetMapping("/{eventId}")
    public EventFullDtoResponse getEvent(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Пришел GET-запрос main-service/users/{userId={}}/events/{eventId={}} без тела", userId, eventId);
        EventFullDtoResponse dto = eventService.makeEventFullDtoResponse(eventService.findEvent(userId, eventId));
        log.info("Ответ на GET-запрос main-service/users/{userId={}}/events/{eventId={}} с телом={}", userId, eventId, dto);
        return dto;
    }

    /**
     * @param userId id текущего пользователя
     * @param eventId id события
     * Получение информации о запросах на участие в событии текущего пользователя
     * В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
     */
    @GetMapping("/{eventId}/requests")
    public Collection<ParticipationRequestDtoResponse> getParticipations(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Пришел GET-запрос main-service/users/{userId={}}/events/{eventId={}}/requests без тела", userId, eventId);
        Collection<ParticipationRequestDtoResponse> dtos = participationRequestMapper.mapParticipationRequestsToDtoResponses(
                eventService.findParticipations(userId, eventId)
        );
        log.info("Ответ на GET-запрос main-service/users/{userId={}}/events/{eventId={}}/requests с телом={}", userId, eventId, dtos);
        return dtos;
    }

    /**
     * @param userId id текущего пользователя
     * @param dto данные добавляемого события
     * Добавление нового события
     * Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventFullDtoResponse createEvent(@PathVariable long userId, @Valid @RequestBody CreateEventDtoRequest dto) {
        log.info("Пришел POST-запрос main-service/users/{userId={}}/events с телом={}", userId, dto);
        EventFullDtoResponse savedDto = eventService.makeEventFullDtoResponse(eventService.createEvent(userId, dto));
        log.info("Ответ на POST-запрос main-service/users/{userId={}}/events с телом={}", userId, savedDto);
        return savedDto;
    }

    /**
     * @param userId id текущего пользователя
     * @param eventId id редактируемого события
     * @param dto новые данные события
     * Изменение события добавленного текущим пользователем
     * - изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
     * - дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента (Ожидается код ошибки 409)
     */
    @PatchMapping("/{eventId}")
    public EventFullDtoResponse updateEvent(@PathVariable long userId, @PathVariable long eventId,
            @Valid @RequestBody PrivateUpdateEventDtoRequest dto) {
        log.info("Пришел PATCH-запрос main-service/users/{userId={}}/events/{eventId={}} с телом={}", userId, eventId, dto);
        EventFullDtoResponse updatedDto = eventService.makeEventFullDtoResponse(eventService.updateEvent(userId, eventId, dto));
        log.info("Ответ на PATCH-запрос main-service/users/{userId={}}/events/{eventId={}} с телом={}", userId, eventId, updatedDto);
        return updatedDto;
    }

    /**
     * @param userId id текущего пользователя
     * @param eventId id события текущего пользователя
     * @param dto новый статус для заявок на участие в событии текущего пользователя
     * Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
     * - если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
     * - нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
     * - статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
     * Если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
     */
    @PatchMapping("/{eventId}/requests")
    public UpdateEventParticipationsDtoResponse updateParticipationsStatus(@PathVariable long userId, @PathVariable long eventId,
                                                                           @Valid @RequestBody UpdateEventParticipationsDtoRequest dto) {
        log.info("Пришел PATCH-запрос main-service/users/{userId={}}/events/{eventId={}}/requests с телом={}", userId, eventId, dto);
        UpdateEventParticipationsDtoResponse updatedDto = eventService.updateParticipationsStatus(userId, eventId, dto);
        log.info("Ответ на PATCH-запрос main-service/users/{userId={}}/events/{eventId={}}/requests с телом={}", userId, eventId, updatedDto);
        return updatedDto;
    }
}
