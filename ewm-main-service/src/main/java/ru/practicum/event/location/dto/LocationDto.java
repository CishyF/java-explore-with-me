package ru.practicum.event.location.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {

    private float lat;
    private float lon;
}
