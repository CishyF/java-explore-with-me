package ru.practicum.service;

import ru.practicum.dto.EndpointHitDtoRequest;
import ru.practicum.dto.ViewStatsDtoResponse;
import ru.practicum.entity.EndpointHit;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatsService {

    EndpointHit createEndpointHit(EndpointHitDtoRequest dto);

    Collection<ViewStatsDtoResponse> findViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
