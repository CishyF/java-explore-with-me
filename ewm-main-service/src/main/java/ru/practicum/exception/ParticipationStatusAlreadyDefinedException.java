package ru.practicum.exception;

public class ParticipationStatusAlreadyDefinedException extends RuntimeException {

    public ParticipationStatusAlreadyDefinedException(String message) {
        super(message);
    }
}
