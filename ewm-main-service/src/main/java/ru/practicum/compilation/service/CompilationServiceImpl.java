package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CreateCompilationDtoRequest;
import ru.practicum.compilation.dto.CompilationDtoResponse;
import ru.practicum.compilation.dto.UpdateCompilationDtoRequest;
import ru.practicum.compilation.entity.Compilation;
import ru.practicum.compilation.mapping.CompilationMapper;
import ru.practicum.compilation.mapping.CompilationPatchUpdater;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.dto.response.EventShortDtoResponse;
import ru.practicum.event.entity.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.EntityNotFoundException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final CompilationPatchUpdater compilationPatchUpdater;
    private final EventService eventService;

    @Override
    public Collection<Compilation> findCompilations(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return compilationRepository.findByPinned(pinned, pageRequest).getContent();
    }

    @Override
    public Compilation findCompilation(long compilationId) {
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Compilation with id=%d was not found", compilationId)));
    }

    @Override
    public Compilation createCompilation(CreateCompilationDtoRequest dto) {
        List<Event> events = fillEvents(dto.getEvents());
        Compilation compilation = compilationMapper.mapDtoRequestToCompilation(events, dto);
        return compilationRepository.save(compilation);
    }

    private List<Event> fillEvents(List<Long> eventIds) {
        List<Event> events = Collections.emptyList();
        if (eventIds != null) {
            events = eventService.findEventsByIdIn(eventIds);
        }
        return events;
    }

    @Override
    public Compilation updateCompilation(long compilationId, UpdateCompilationDtoRequest dto) {
        Compilation compilation = findCompilation(compilationId);
        List<Event> events = fillEvents(dto.getEvents());
        compilationPatchUpdater.updateCompilation(events, compilation, dto);
        return compilationRepository.save(compilation);
    }

    @Override
    public void deleteCompilation(long compilationId) {
        Compilation compilation = findCompilation(compilationId);
        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDtoResponse makeCompilationDtoResponse(Compilation compilation) {
        CompilationDtoResponse dto = compilationMapper.mapCompilationToDtoResponse(compilation);
        List<EventShortDtoResponse> eventDtos = fillEventDtos(compilation.getEvents());
        dto.setEvents(eventDtos);
        return dto;
    }

    private List<EventShortDtoResponse> fillEventDtos(List<Event> events) {
        List<EventShortDtoResponse> eventDtos = Collections.emptyList();
        if (events != null) {
            eventDtos = events.stream()
                    .map(eventService::makeEventShortDtoResponse).collect(Collectors.toList());
        }
        return eventDtos;
    }
}
