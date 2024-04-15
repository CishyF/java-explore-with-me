package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDtoResponse;
import ru.practicum.compilation.service.CompilationService;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {

    private final CompilationService compilationService;

    /**
     * @param pinned искать только закрепленные/не закрепленные подборки
     * @param from количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size количество элементов в наборе
     * Получение подборок событий
     * В случае, если по заданным фильтрам не найдено ни одной подборки, возвращает пустой список
     */
    @GetMapping
    public Collection<CompilationDtoResponse> getCompilations(@RequestParam(required = false) Boolean pinned,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Пришел GET-запрос main-service/compilations?pinned={}&from={}&size={} без тела", pinned, from, size);
        Collection<CompilationDtoResponse> dtos = compilationService.findCompilations(pinned, from, size)
                .stream().map(compilationService::makeCompilationDtoResponse).collect(Collectors.toList());
        log.info("Ответ на GET-запрос main-service/compilations?pinned={}&from={}&size={} с телом={}", pinned, from, size, dtos);
        return dtos;
    }

    /**
     * @param compilationId id подборки
     * Получение подборки событий по его id
     * В случае, если подборки с заданным id не найдено, возвращает статус код 404
     */
    @GetMapping("/{compilationId}")
    public CompilationDtoResponse getCompilation(@PathVariable long compilationId) {
        log.info("Пришел GET-запрос main-service/compilations/{compilationId={}} без тела", compilationId);
        CompilationDtoResponse dto = compilationService.makeCompilationDtoResponse(compilationService.findCompilation(compilationId));
        log.info("Ответ на GET-запрос main-service/compilations/{compilationId={}} с телом={}", compilationId, dto);
        return dto;
    }
}
