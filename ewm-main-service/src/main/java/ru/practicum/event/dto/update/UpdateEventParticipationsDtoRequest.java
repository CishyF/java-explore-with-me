package ru.practicum.event.dto.update;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventParticipationsDtoRequest {

    @NotNull
    List<Long> requestIds;

    @NotNull
    UpdateEventParticipationStatus status;
}
