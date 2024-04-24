package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.dto.CommentDtoResponse;
import ru.practicum.comment.mapping.CommentMapper;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentController {

    private final CommentMapper commentMapper;
    private final CommentService commentService;

    /**
     * @param userId id пользователя
     * Получение всех комментариев, оставленных пользователем
     */
    @GetMapping
    public Collection<CommentDtoResponse> getComments(@PathVariable long userId) {
        log.info("Пришел GET-запрос main-service/users/{userId={}}/comments без тела", userId);
        Collection<CommentDtoResponse> dtos = commentMapper.mapCommentsToDtoResponses(commentService.findComments(userId));
        log.info("Ответ на GET-запрос main-service/users/{userId={}}/comments с телом={}", userId, dtos);
        return dtos;
    }

    /**
     * @param userId id пользователя
     * @param commentId id комментария
     * @param dto данные обновленного комментария
     * Обновление комментария конкретного пользователя
     * id пользователя должен совпадать с id автора
     */
    @PatchMapping("/{commentId}")
    public CommentDtoResponse updateComment(@PathVariable long userId,
                @PathVariable long commentId, @Valid @RequestBody CommentDtoRequest dto) {
        log.info("Пришел PATCH-запрос main-service/users/{userId={}}/comments/{commentId={}} с телом={}", userId, commentId, dto);
        CommentDtoResponse updatedDto = commentMapper.mapCommentToDtoResponse(commentService.updateComment(commentId, userId, dto));
        log.info("Ответ на PATCH-запрос main-service/users/{userId={}}/comments/{commentId={}} с телом={}", userId, commentId, updatedDto);
        return updatedDto;
    }

    /**
     * @param userId id пользователя
     * @param commentId id комментария
     * Удаление комментария по id
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable long userId, @PathVariable long commentId) {
        log.info("Пришел DELETE-запрос main-service/users/{userId={}}/comments/{commentId={}} без тела", userId, commentId);
        commentService.deleteComment(commentId, userId);
        log.info("Ответ на DELETE-запрос main-service/users/{userId={}}/comments/{commentId={}} без тела", userId, commentId);
    }
}
