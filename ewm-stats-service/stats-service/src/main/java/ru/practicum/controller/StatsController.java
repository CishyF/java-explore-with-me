package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDtoRequest;
import ru.practicum.dto.EndpointHitDtoResponse;
import ru.practicum.dto.ViewStatsDtoResponse;
import ru.practicum.mapping.StatsMapper;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.util.Constants.DATE_TIME_PATTERN;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;
    private final StatsMapper statsMapper;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDtoResponse createEndpointHit(@Valid @RequestBody EndpointHitDtoRequest dto) {
        log.info("Пришел POST-запрос stats-service/hit с телом={}", dto);
        EndpointHitDtoResponse savedDto = statsMapper.mapEndpointHitToDtoResponse(statsService.createEndpointHit(dto));
        log.info("Ответ на POST-запрос stats-service/hit с телом={}", savedDto);
        return savedDto;
    }

    @GetMapping("/stats")
    public Collection<ViewStatsDtoResponse> getViewStats(
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") boolean unique
    ) {
        log.info("Пришел GET-запрос stats-service/stats?start={}&end={}&uris={}&unique={}", start, end, uris, unique);
        Collection<ViewStatsDtoResponse> viewStatsDtos = statsService.findViewStats(start, end, uris, unique);
        log.info("Ответ на GET-запрос stats-service/stats?start={}&end={}&uris={}&unique={} с телом={}", start, end, uris, unique, viewStatsDtos);
        return viewStatsDtos;
    }
}
