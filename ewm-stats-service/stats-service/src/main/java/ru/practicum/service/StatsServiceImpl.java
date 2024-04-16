package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDtoRequest;
import ru.practicum.dto.ViewStatsDtoResponse;
import ru.practicum.entity.EndpointHit;
import ru.practicum.exception.IncorrectRequestException;
import ru.practicum.mapping.StatsMapper;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Override
    public EndpointHit createEndpointHit(EndpointHitDtoRequest dto) {
        EndpointHit endpointHit = statsMapper.mapDtoRequestToEndpointHit(dto);
        return statsRepository.save(endpointHit);
    }

    @Override
    public Collection<ViewStatsDtoResponse> findViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new IncorrectRequestException("The specified date range is incorrect");
        }
        if (uris == null || uris.isEmpty()) {
            return findViewStatsOfAllUris(start, end, unique);
        } else if (unique) {
            return findUniqueViewStatsWithCustomUris(start, end, uris);
        }
        return statsRepository.findByUrisAndBetweenStartAndEnd(uris, start, end);
    }

    private Collection<ViewStatsDtoResponse> findViewStatsOfAllUris(LocalDateTime start, LocalDateTime end, boolean unique) {
        if (unique) {
            return statsRepository.findAllUniqueBetweenStartAndEnd(start, end);
        }
        return statsRepository.findAllBetweenStartAndEnd(start, end);
    }

    private Collection<ViewStatsDtoResponse> findUniqueViewStatsWithCustomUris(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return statsRepository.findUniqueByUrisAndBetweenStartAndEnd(uris, start, end);
    }
}
