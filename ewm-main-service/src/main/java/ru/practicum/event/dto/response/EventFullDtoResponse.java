package ru.practicum.event.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.category.dto.CategoryDtoResponse;
import ru.practicum.comment.dto.CommentDtoResponse;
import ru.practicum.event.entity.EventState;
import ru.practicum.event.location.dto.LocationDto;
import ru.practicum.user.dto.UserShortDtoResponse;
import ru.practicum.util.Constants;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDtoResponse {

    private long id;
    private String title;
    private String annotation;
    private CategoryDtoResponse category;
    private long confirmedRequests;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime eventDate;
    private UserShortDtoResponse initiator;
    private LocationDto location;
    private boolean paid;
    private int participantLimit;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private EventState state;
    private long views;
    private List<CommentDtoResponse> comments;
}
