package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CreateCompilationDtoRequest;
import ru.practicum.compilation.dto.CompilationDtoResponse;
import ru.practicum.compilation.dto.UpdateCompilationDtoRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final CompilationService compilationService;

    /**
     * @param dto данные новой подборки
     * Добавление новой подборки (подборка может не содержать событий)
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CompilationDtoResponse createCompilation(@Valid @RequestBody CreateCompilationDtoRequest dto) {
        log.info("Пришел POST-запрос main-service/admin/compilations с телом={}", dto);
        CompilationDtoResponse savedDto = compilationService.makeCompilationDtoResponse(compilationService.createCompilation(dto));
        log.info("Ответ на POST-запрос main-service/admin/compilations с телом={}", savedDto);
        return savedDto;
    }

    /**
     * @param compilationId id подборки
     * @param dto данные для обновления подборки
     * Обновить информацию о подборке
     */
    @PatchMapping("/{compilationId}")
    public CompilationDtoResponse updateCompilation(@PathVariable long compilationId, @Valid @RequestBody UpdateCompilationDtoRequest dto) {
        log.info("Пришел PATCH-запрос main-service/admin/compilations/{compilationId={}} с телом={}", compilationId, dto);
        CompilationDtoResponse updatedDto = compilationService.makeCompilationDtoResponse(compilationService.updateCompilation(compilationId, dto));
        log.info("Ответ на PATCH-запрос main-service/admin/compilations/{compilationId={}} с телом={}", compilationId, updatedDto);
        return updatedDto;
    }

    /**
     * @param compilationId id подборки
     * Удаление подборки
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{compilationId}")
    public void deleteCompilation(@PathVariable long compilationId) {
        log.info("Пришел DELETE-запрос main-service/admin/compilations/{compilationId={}} без тела", compilationId);
        compilationService.deleteCompilation(compilationId);
        log.info("Ответ на DELETE-запрос main-service/admin/compilations/{compilationId={}} без тела", compilationId);
    }
}
