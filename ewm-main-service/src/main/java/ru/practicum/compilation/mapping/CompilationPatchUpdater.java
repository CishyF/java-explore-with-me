package ru.practicum.compilation.mapping;

import org.mapstruct.*;
import ru.practicum.compilation.dto.UpdateCompilationDtoRequest;
import ru.practicum.compilation.entity.Compilation;
import ru.practicum.event.entity.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationPatchUpdater {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCompilation(@MappingTarget Compilation compilation, UpdateCompilationDtoRequest dto);

    default void updateCompilation(List<Event> events, Compilation compilation, UpdateCompilationDtoRequest dto) {
        updateCompilation(compilation, dto);
        if (events != null && !events.isEmpty()) {
            compilation.setEvents(events);
        }
    }
}
