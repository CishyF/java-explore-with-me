package ru.practicum.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentDtoRequest {

    @NotBlank(message = "Field: text. Error: must not be blank.")
    @Size(min = 1, max = 7000, message = "Field: title. Error: must be in defined bounds of size.")
    String text;
}
