package ru.practicum.event.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.category.dto.CategoryDtoResponse;
import ru.practicum.user.dto.UserShortDtoResponse;
import ru.practicum.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDtoResponse {

    private long id;
    private String title;
    private String annotation;
    private CategoryDtoResponse category;
    private long confirmedRequests;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime eventDate;
    private UserShortDtoResponse initiator;
    private boolean paid;
    private long views;
}
