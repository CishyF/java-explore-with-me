package ru.practicum.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoRequest {

    @NotBlank(message = "Field: name. Error: must not be blank.")
    @Size(min = 2, max = 250, message = "Field: name. Error: must be in defined bounds of size.")
    private String name;

    @Email
    @NotBlank(message = "Field: email. Error: must not be blank.")
    @Size(min = 6, max = 254, message = "Field: email. Error: must be in defined bounds of size.")
    private String email;
}
