package ru.practicum.category.mapping;

import org.mapstruct.Mapper;
import ru.practicum.category.dto.CategoryDtoRequest;
import ru.practicum.category.dto.CategoryDtoResponse;
import ru.practicum.category.entity.Category;

import java.util.Collection;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category mapDtoRequestToCategory(CategoryDtoRequest dto);

    CategoryDtoResponse mapCategoryToDtoResponse(Category category);

    default Collection<CategoryDtoResponse> mapCategoriesToDtoResponses(Collection<Category> categories) {
        return categories.stream().map(this::mapCategoryToDtoResponse).collect(Collectors.toList());
    }
}
