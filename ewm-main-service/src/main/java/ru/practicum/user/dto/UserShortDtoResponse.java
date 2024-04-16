package ru.practicum.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDtoResponse {

    private long id;
    private String name;
}
