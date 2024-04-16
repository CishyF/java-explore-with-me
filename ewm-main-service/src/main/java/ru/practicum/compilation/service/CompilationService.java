package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CreateCompilationDtoRequest;
import ru.practicum.compilation.dto.CompilationDtoResponse;
import ru.practicum.compilation.dto.UpdateCompilationDtoRequest;
import ru.practicum.compilation.entity.Compilation;

import java.util.Collection;

public interface CompilationService {

    Collection<Compilation> findCompilations(Boolean pinned, int from, int size);

    Compilation findCompilation(long compilationId);

    Compilation createCompilation(CreateCompilationDtoRequest dto);

    Compilation updateCompilation(long compilationId, UpdateCompilationDtoRequest dto);

    void deleteCompilation(long compilationId);

    CompilationDtoResponse makeCompilationDtoResponse(Compilation compilation);
}
