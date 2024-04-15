package ru.practicum.exception.handling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.*;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            TypeMismatchException.class, MissingRequestValueException.class,
            MethodArgumentNotValidException.class, IncorrectRequestException.class,
            EventDateIsInvalidException.class
    })
    public ErrorResponse handleBadRequest(RuntimeException e) {
        log.error("Обработка исключения с кодом 400", e);
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleNotFound(RuntimeException e) {
        log.error("Обработка исключения с кодом 404", e);
        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("The required object was not found.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({
            DataAccessException.class, ParticipantLimitExceededException.class,
            EventIsNotPublishedException.class, ParticipationInOwnEventException.class,
            AccessDeniedException.class, CategoryIsNotEmptyException.class
    })
    public ErrorResponse handleConstraintViolation(RuntimeException e) {
        log.error("Обработка исключения с кодом 409", e);
        return ErrorResponse.builder()
                .status(HttpStatus.CONFLICT)
                .reason("Integrity constraint has been violated.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleInternalServerError(Throwable t) {
        log.error("Обработка ошибки с кодом 500", t);
        return ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .reason("Server error.")
                .message(t.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
