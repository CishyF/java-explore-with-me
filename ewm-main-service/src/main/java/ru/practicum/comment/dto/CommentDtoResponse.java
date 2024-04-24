package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentDtoResponse {

    private long id;
    private String text;
    private long authorId;
    private long eventId;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime createdAt;
}
