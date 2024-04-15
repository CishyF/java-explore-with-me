package ru.practicum.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoResponse {

    private long id;
    private String name;
    private String email;
}
