package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDtoRequest;
import ru.practicum.category.dto.CategoryDtoResponse;
import ru.practicum.category.mapping.CategoryMapper;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    /**
     * @param dto данные добавляемой категории
     * Добавление новой категории
     * Имя категории должно быть уникальным
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryDtoResponse createCategory(@Valid @RequestBody CategoryDtoRequest dto) {
        log.info("Пришел POST-запрос main-service/admin/categories с телом={}", dto);
        CategoryDtoResponse savedDto = categoryMapper.mapCategoryToDtoResponse(categoryService.createCategory(dto));
        log.info("Ответ на POST-запрос main-service/admin/categories с телом={}", savedDto);
        return savedDto;
    }

    /**
     * @param categoryId id категории
     * @param dto данные категории для изменения
     * Изменение категории
     * Имя категории должно быть уникальным
     */
    @PatchMapping("/{categoryId}")
    public CategoryDtoResponse updateCategory(@PathVariable long categoryId, @Valid @RequestBody CategoryDtoRequest dto) {
        log.info("Пришел PATCH-запрос main-service/admin/categories/{categoryId={}} с телом={}", categoryId, dto);
        CategoryDtoResponse updatedDto = categoryMapper.mapCategoryToDtoResponse(categoryService.updateCategory(categoryId, dto));
        log.info("Ответ на PATCH-запрос main-service/admin/categories/{categoryId={}} с телом={}", categoryId, updatedDto);
        return updatedDto;
    }

    /**
     * @param categoryId id категории
     * Удаление категории
     * С категорией не должно быть связано ни одного события
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{categoryId}")
    public void deleteCategory(@PathVariable long categoryId) {
        log.info("Пришел DELETE-запрос main-service/admin/categories/{categoryId={}} без тела", categoryId);
        categoryService.deleteCategory(categoryId);
        log.info("Ответ на DELETE-запрос main-service/admin/categories/{categoryId={}} без тела", categoryId);
    }
}
