package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.response.EventFullDtoResponse;
import ru.practicum.event.dto.update.AdminUpdateEventDtoRequest;
import ru.practicum.event.entity.EventState;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.util.Constants.DATE_TIME_PATTERN;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService eventService;

    /**
     * @param users список id пользователей, чьи события нужно найти
     * @param states список состояний в которых находятся искомые события
     * @param categories список id категорий в которых будет вестись поиск
     * @param rangeStart дата и время не раньше которых должно произойти событие
     * @param rangeEnd дата и время не позже которых должно произойти событие
     * @param from количество событий, которые нужно пропустить для формирования текущего набора
     * @param size количество событий в наборе
     * Поиск событий
     * Эндпоинт возвращает полную информацию обо всех событиях подходящих под переданные условия
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     */
    @GetMapping
    public Collection<EventFullDtoResponse> getEvents(@RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states, @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Пришел GET-запрос main-service/admin/events?users={}&categories={}&rangeStart={}&rangeEnd={}&from={}&size={} без тела",
                users, categories, rangeStart, rangeEnd, from, size);
        Collection<EventFullDtoResponse> dtos = eventService.findEvents(users, states, categories, rangeStart, rangeEnd, from, size)
                .stream().map(eventService::makeEventFullDtoResponse).collect(Collectors.toList());
        log.info("Ответ на GET-запрос main-service/admin/events?users={}&categories={}&rangeStart={}&rangeEnd={}&from={}&size={} с телом={}",
                users, categories, rangeStart, rangeEnd, from, size, dtos);
        return dtos;
    }

    /**
     * @param eventId id события
     * @param dto данные для изменения информации о событии
     * Редактирование данных события и его статуса (отклонение/публикация)
     * Редактирование данных любого события администратором. Валидация данных не требуется
     * - дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
     * - событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
     * - событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
     */
    @PatchMapping("/{eventId}")
    public EventFullDtoResponse updateEvent(@PathVariable long eventId, @Valid @RequestBody AdminUpdateEventDtoRequest dto) {
        log.info("Пришел PATCH-запрос main-service/admin/events/{eventId={}} с телом={}", eventId, dto);
        EventFullDtoResponse updatedDto = eventService.makeEventFullDtoResponse(eventService.updateEvent(eventId, dto));
        log.info("Ответ на PATCH-запрос main-service/admin/events/{eventId={}} с телом={}", eventId, updatedDto);
        return updatedDto;
    }
}
