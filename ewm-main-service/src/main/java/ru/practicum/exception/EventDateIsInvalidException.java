package ru.practicum.exception;

public class EventDateIsInvalidException extends RuntimeException {

    public EventDateIsInvalidException(String message) {
        super(message);
    }
}
