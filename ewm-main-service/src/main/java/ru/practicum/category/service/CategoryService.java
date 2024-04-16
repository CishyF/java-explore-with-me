package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDtoRequest;
import ru.practicum.category.entity.Category;

import java.util.Collection;

public interface CategoryService {

    Collection<Category> findCategories(int from, int size);

    Category findCategory(long categoryId);

    Category createCategory(CategoryDtoRequest dto);

    Category updateCategory(long categoryId, CategoryDtoRequest dto);

    void deleteCategory(long categoryId);
}
