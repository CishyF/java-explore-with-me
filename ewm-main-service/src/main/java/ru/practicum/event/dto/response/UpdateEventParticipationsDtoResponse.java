package ru.practicum.event.dto.response;

import lombok.*;
import ru.practicum.participation.dto.ParticipationRequestDtoResponse;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventParticipationsDtoResponse {

    List<ParticipationRequestDtoResponse> confirmedRequests;
    List<ParticipationRequestDtoResponse> rejectedRequests;
}
