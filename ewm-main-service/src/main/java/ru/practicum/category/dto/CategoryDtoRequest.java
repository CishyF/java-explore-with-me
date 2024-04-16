package ru.practicum.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDtoRequest {

    @NotBlank(message = "Field: name. Error: must not be blank.")
    @Size(min = 1, max = 50, message = "Field: name. Error: must be in defined bounds of size.")
    private String name;
}
