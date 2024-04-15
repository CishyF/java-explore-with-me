package ru.practicum.compilation.dto;

import lombok.*;
import ru.practicum.event.dto.response.EventShortDtoResponse;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDtoResponse {

    private long id;
    private String title;
    private List<EventShortDtoResponse> events;
    private boolean pinned;
}
