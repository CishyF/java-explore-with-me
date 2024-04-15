package ru.practicum.exception.handling;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static ru.practicum.util.Constants.DATE_TIME_PATTERN;

@Value
@Builder
public class ErrorResponse {

    String message;
    String reason;
    HttpStatus status;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime timestamp;
}

