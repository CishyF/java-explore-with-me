package ru.practicum.event.dto.update;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.event.location.dto.LocationDto;
import ru.practicum.util.Constants;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateEventDtoRequest {

    @Size(min = 3, max = 120, message = "Field: title. Error: must be in defined bounds of size.")
    private String title;

    @Size(min = 20, max = 2000, message = "Field: annotation. Error: must be in defined bounds of size.")
    private String annotation;

    @Positive
    private Long category;

    @Size(min = 20, max = 7000, message = "Field: description. Error: must be in defined bounds of size.")
    private String description;

    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private AdminUpdateEventStateAction stateAction;
}
