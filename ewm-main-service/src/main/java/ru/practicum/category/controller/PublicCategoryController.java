package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDtoResponse;
import ru.practicum.category.mapping.CategoryMapper;
import ru.practicum.category.service.CategoryService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    /**
     * @param from количество категорий, которые нужно пропустить для формирования текущего набора
     * @param size количество категорий в наборе
     * Получение категорий
     * В случае, если по заданным фильтрам не найдено ни одной категории, возвращает пустой список
     */
    @GetMapping
    public Collection<CategoryDtoResponse> getCategories(@RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Пришел GET-запрос main-service/categories?from={}&size={} без тела", from, size);
        Collection<CategoryDtoResponse> dtos = categoryMapper.mapCategoriesToDtoResponses(
                categoryService.findCategories(from, size)
        );
        log.info("Ответ на GET-запрос main-service/categories?from={}&size={} с телом={}", from, size, dtos);
        return dtos;
    }

    /**
     * @param categoryId id категории
     * Получение информации о категории по её идентификатору
     * В случае, если категории с заданным id не найдено, возвращает статус код 404
     */
    @GetMapping("/{categoryId}")
    public CategoryDtoResponse getCategory(@PathVariable long categoryId) {
        log.info("Пришел GET-запрос main-service/categories/{categoryId={}} без тела", categoryId);
        CategoryDtoResponse dto = categoryMapper.mapCategoryToDtoResponse(categoryService.findCategory(categoryId));
        log.info("Ответ на GET-запрос main-service/categories/{categoryId={}} с телом={}", categoryId, dto);
        return dto;
    }
}
