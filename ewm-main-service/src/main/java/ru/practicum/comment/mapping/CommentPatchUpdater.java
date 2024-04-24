package ru.practicum.comment.mapping;

import org.mapstruct.*;
import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.entity.Comment;

@Mapper(componentModel = "spring")
public interface CommentPatchUpdater {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateComment(@MappingTarget Comment comment, CommentDtoRequest dto);
}
