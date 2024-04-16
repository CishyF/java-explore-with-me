package ru.practicum.participation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.participation.dto.ParticipationRequestDtoResponse;
import ru.practicum.participation.mapping.ParticipationRequestMapper;
import ru.practicum.participation.service.ParticipationRequestService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateParticipationRequestController {

    private final ParticipationRequestMapper participationRequestMapper;
    private final ParticipationRequestService participationRequestService;

    /**
     * @param userId id текущего пользователя
     * Получение информации о заявках текущего пользователя на участие в чужих событиях
     * В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
     */
    @GetMapping
    public Collection<ParticipationRequestDtoResponse> getParticipationRequests(@PathVariable long userId) {
        log.info("Пришел GET-запрос main-service/users/{userId={}}/requests без тела", userId);
        Collection<ParticipationRequestDtoResponse> dtos = participationRequestMapper.mapParticipationRequestsToDtoResponses(
                participationRequestService.findParticipationRequestsByUserId(userId)
        );
        log.info("Ответ на GET-запрос main-service/users/{userId={}}/requests с телом={}", userId, dtos);
        return dtos;
    }

    /**
     * @param userId id текущего пользователя
     * @param eventId id события
     * Добавление запроса от текущего пользователя на участие в событии.
     * - нельзя добавить повторный запрос (Ожидается код ошибки 409)
     * - инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
     * - нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
     * - если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
     * Если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ParticipationRequestDtoResponse createParticipationRequest(@PathVariable long userId, @RequestParam long eventId) {
        log.info("Пришел POST-запрос main-service/users/{userId={}}/requests?eventId={} без тела", userId, eventId);
        ParticipationRequestDtoResponse savedDto = participationRequestMapper.mapParticipationRequestToDtoResponse(
                participationRequestService.createParticipationRequest(userId, eventId)
        );
        log.info("Ответ на POST-запрос main-service/users/{userId={}}/requests?eventId={} с телом={}", userId, eventId, savedDto);
        return savedDto;
    }

    /**
     * @param userId id текущего пользователя
     * @param requestId id запроса на участие
     * Отмена своего запроса на участие в событии
     */
    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDtoResponse cancelParticipationRequest(@PathVariable long userId, @PathVariable long requestId) {
        log.info("Пришел PATCH-запрос main-service/users/{userId={}}/requests/{requestId={}}/cancel без тела", userId, requestId);
        ParticipationRequestDtoResponse dto = participationRequestMapper.mapParticipationRequestToDtoResponse(
                participationRequestService.cancelParticipationRequest(userId, requestId)
        );
        log.info("Ответ на PATCH-запрос main-service/users/{userId={}}/requests/{requestId={}}/cancel с телом={}", userId, requestId, dto);
        return dto;
    }
}
