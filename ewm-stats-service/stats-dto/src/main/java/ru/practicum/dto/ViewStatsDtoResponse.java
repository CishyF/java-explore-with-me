package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ViewStatsDtoResponse {

    private String app;
    private String uri;
    private long hits;
}
