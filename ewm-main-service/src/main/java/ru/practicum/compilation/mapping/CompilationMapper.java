package ru.practicum.compilation.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilation.dto.CreateCompilationDtoRequest;
import ru.practicum.compilation.dto.CompilationDtoResponse;
import ru.practicum.compilation.entity.Compilation;
import ru.practicum.event.entity.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "events", ignore = true)
    Compilation mapDtoRequestToCompilation(CreateCompilationDtoRequest dto);

    default Compilation mapDtoRequestToCompilation(List<Event> events, CreateCompilationDtoRequest dto) {
        Compilation compilation = mapDtoRequestToCompilation(dto);
        compilation.setEvents(events);
        return compilation;
    }

    @Mapping(target = "events", ignore = true)
    CompilationDtoResponse mapCompilationToDtoResponse(Compilation compilation);
}
