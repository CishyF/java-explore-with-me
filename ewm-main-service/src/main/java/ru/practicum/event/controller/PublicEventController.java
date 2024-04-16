package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.response.EventFullDtoResponse;
import ru.practicum.event.dto.response.EventShortDtoResponse;
import ru.practicum.event.service.EventService;
import ru.practicum.stats.StatsProxy;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.util.Constants.DATE_TIME_PATTERN;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;
    private final StatsProxy statsProxy;

    /**
     * @param text текст для поиска в содержимом аннотации и подробном описании события
     * @param categories список идентификаторов категорий в которых будет вестись поиск
     * @param paid поиск только платных/бесплатных событий
     * @param rangeStart дата и время не раньше которых должно произойти событие
     * @param rangeEnd дата и время не позже которых должно произойти событие
     * @param onlyAvailable только события у которых не исчерпан лимит запросов на участие
     * @param sort вариант сортировки: по дате события или по количеству просмотров
     * @param from количество событий, которые нужно пропустить для формирования текущего набора
     * @param size количество событий в наборе
     * @param request полученный запрос
     * Получение событий с возможностью фильтрации
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     * - это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
     * - текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
     * - если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
     * - информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
     * - информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
     */
    @GetMapping
    public Collection<EventShortDtoResponse> getEvents(@RequestParam(required = false) String text,
                                                       @RequestParam(required = false) @Positive List<Long> categories, @RequestParam(required = false) Boolean paid,
                                                       @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeStart,
                                                       @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeEnd,
                                                       @RequestParam(required = false, defaultValue = "false") boolean onlyAvailable,
                                                       @RequestParam(required = false) SortType sort,
                                                       @RequestParam(required = false, defaultValue = "0") int from,
                                                       @RequestParam(required = false, defaultValue = "10") int size, HttpServletRequest request) {
        log.info("Пришел GET-запрос main-service/events?text={}&categories={}&paid={}&rangeStart={}&rangeEnd={}&onlyAvailable={}&sort={}&from={}&size={} без тела",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        Collection<EventShortDtoResponse> dtos = eventService.findEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size
                ).stream()
                .map(eventService::makeEventShortDtoResponse)
                .collect(Collectors.toList());
        statsProxy.addHit(request.getRequestURI(), request.getRemoteAddr());
        log.info("Ответ на GET-запрос main-service/events?text={}&categories={}&paid={}&rangeStart={}&rangeEnd={}&onlyAvailable={}&sort={}&from={}&size={} c телом={}",
                                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, dtos);
        return dtos;
    }

    /**
     * @param eventId id события
     * @param request полученный запрос
     * событие должно быть опубликовано
     * информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
     * информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
     * В случае, если события с заданным id не найдено, возвращает статус код 404
     */
    @GetMapping("/{eventId}")
    public EventFullDtoResponse getEvent(@PathVariable long eventId, HttpServletRequest request) {
        log.info("Пришел GET-запрос main-service/events/{eventId={}} без тела", eventId);
        statsProxy.addHit(request.getRequestURI(), request.getRemoteAddr());
        EventFullDtoResponse dto = eventService.makeEventFullDtoResponse(eventService.findPublishedEvent(eventId));
        log.info("Ответ на GET-запрос main-service/events/{eventId={}} с телом={}", eventId, dto);
        return dto;
    }
}
