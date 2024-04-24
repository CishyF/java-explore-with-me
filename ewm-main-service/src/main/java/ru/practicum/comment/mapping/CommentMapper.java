package ru.practicum.comment.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.dto.CommentDtoResponse;
import ru.practicum.comment.entity.Comment;
import ru.practicum.event.entity.Event;
import ru.practicum.user.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment mapDtoRequestToComment(CommentDtoRequest dto);

    default Comment mapDtoRequestToComment(Event event, User author, CommentDtoRequest dto) {
        Comment comment = mapDtoRequestToComment(dto);
        comment.setEvent(event);
        comment.setAuthor(author);
        return comment;
    }

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "event.id", target = "eventId")
    CommentDtoResponse mapCommentToDtoResponse(Comment comment);

    default List<CommentDtoResponse> mapCommentsToDtoResponses(Collection<Comment> comments) {
        return comments.stream().map(this::mapCommentToDtoResponse).collect(Collectors.toList());
    }
}
