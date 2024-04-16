package ru.practicum.category.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDtoResponse {

    private long id;
    private String name;
}
