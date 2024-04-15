package ru.practicum.event.dto.create;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.event.location.dto.LocationDto;
import ru.practicum.util.Constants;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventDtoRequest {

    @NotBlank(message = "Field: title. Error: must not be blank.")
    @Size(min = 3, max = 120, message = "Field: title. Error: must be in defined bounds of size.")
    private String title;

    @NotBlank(message = "Field: annotation. Error: must not be blank.")
    @Size(min = 20, max = 2000, message = "Field: annotation. Error: must be in defined bounds of size.")
    private String annotation;

    @Positive
    private long category;

    @NotBlank(message = "Field: description. Error: must not be blank.")
    @Size(min = 20, max = 7000, message = "Field: description. Error: must be in defined bounds of size.")
    private String description;

    @Future
    @NotNull
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    private boolean paid;

    @PositiveOrZero
    private int participantLimit;

    private boolean requestModeration = true;
}
