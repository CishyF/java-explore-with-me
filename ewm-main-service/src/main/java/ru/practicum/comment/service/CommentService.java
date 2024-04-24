package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.entity.Comment;
import ru.practicum.event.entity.Event;

import java.util.Collection;
import java.util.List;

public interface CommentService {

    Collection<Comment> findComments(long authorId);

    List<Comment> findComments(Event event);

    Comment findComment(long commentId);

    Comment createComment(Event event, long authorId, CommentDtoRequest dto);

    Comment updateComment(long commentId, long authorId, CommentDtoRequest dto);

    void deleteComment(long commentId, long authorId);
}
