package ru.practicum.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompilationDtoRequest {

    @NotBlank(message = "Field: title. Error: must not be blank.")
    @Size(min = 1, max = 50, message = "Field: title. Error: must be in defined bounds of size.")
    private String title;
    private List<Long> events;
    private boolean pinned;
}
