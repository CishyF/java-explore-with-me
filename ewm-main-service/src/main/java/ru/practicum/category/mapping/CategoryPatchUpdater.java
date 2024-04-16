package ru.practicum.category.mapping;

import org.mapstruct.*;
import ru.practicum.category.dto.CategoryDtoRequest;
import ru.practicum.category.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryPatchUpdater {

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategory(@MappingTarget Category category, CategoryDtoRequest dto);
}
