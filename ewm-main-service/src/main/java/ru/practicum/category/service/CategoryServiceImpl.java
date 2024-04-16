package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDtoRequest;
import ru.practicum.category.entity.Category;
import ru.practicum.category.mapping.CategoryMapper;
import ru.practicum.category.mapping.CategoryPatchUpdater;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.CategoryIsNotEmptyException;
import ru.practicum.exception.EntityNotFoundException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryPatchUpdater categoryPatchUpdater;
    private final EventRepository eventRepository;

    @Override
    public Collection<Category> findCategories(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageRequest).getContent();
    }

    @Override
    public Category findCategory(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Category with id=%d was not found", categoryId)));
    }

    @Override
    public Category createCategory(CategoryDtoRequest dto) {
        Category category = categoryMapper.mapDtoRequestToCategory(dto);
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(long categoryId, CategoryDtoRequest dto) {
        Category category = findCategory(categoryId);
        categoryPatchUpdater.updateCategory(category, dto);
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(long categoryId) {
        Category category = findCategory(categoryId);
        if (eventRepository.findUniqueNameCategoriesOfEvents().stream().anyMatch(category.getName()::equals)) {
            throw new CategoryIsNotEmptyException("The category is not empty");
        }
        categoryRepository.delete(category);
    }
}
