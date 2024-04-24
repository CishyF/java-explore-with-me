package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.entity.Comment;
import ru.practicum.comment.mapping.CommentMapper;
import ru.practicum.comment.mapping.CommentPatchUpdater;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.EventState;
import ru.practicum.exception.AccessDeniedException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.EventIsNotPublishedException;
import ru.practicum.user.entity.User;
import ru.practicum.user.service.UserService;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentPatchUpdater commentPatchUpdater;
    private final UserService userService;

    @Override
    public Collection<Comment> findComments(long authorId) {
        User author = userService.findUser(authorId);
        return commentRepository.findCommentsByAuthor(author);
    }

    @Override
    public List<Comment> findComments(Event event) {
        return commentRepository.findCommentsByEvent(event);
    }

    @Override
    public Comment findComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with id=%d was not found", commentId)));
    }

    @Override
    public Comment createComment(Event event, long authorId, CommentDtoRequest dto) {
        User author = userService.findUser(authorId);
        if (event.getState() != EventState.PUBLISHED) {
            throw new EventIsNotPublishedException(String.format("Can't add comment for unpublished event with id=%d", event.getId()));
        }
        Comment comment = commentMapper.mapDtoRequestToComment(event, author, dto);
        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(long commentId, long authorId, CommentDtoRequest dto) {
        Comment comment = findComment(commentId);
        if (comment.getAuthor().getId() != authorId) {
            throw new AccessDeniedException(String.format("User with id=%d can't update comment with id=%d", authorId, commentId));
        }
        commentPatchUpdater.updateComment(comment, dto);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(long commentId, long authorId) {
        Comment comment = findComment(commentId);
        if (comment.getAuthor().getId() != authorId) {
            throw new AccessDeniedException(String.format("User with id=%d can't delete comment with id=%d", authorId, commentId));
        }
        commentRepository.delete(comment);
    }
}
