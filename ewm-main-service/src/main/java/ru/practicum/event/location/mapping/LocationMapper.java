package ru.practicum.event.location.mapping;

import org.mapstruct.Mapper;
import ru.practicum.event.location.dto.LocationDto;
import ru.practicum.event.location.entity.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location mapDtoToLocation(LocationDto locationDto);

    LocationDto mapLocationToDto(Location location);
}
