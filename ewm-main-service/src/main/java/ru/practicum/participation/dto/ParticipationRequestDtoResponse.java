package ru.practicum.participation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.participation.entity.ParticipationRequestStatus;
import ru.practicum.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDtoResponse {

    private long id;
    private long event;
    private long requester;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime created;
    private ParticipationRequestStatus status;
}
