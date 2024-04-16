package ru.practicum.user.mapping;

import org.mapstruct.Mapper;
import ru.practicum.user.dto.UserDtoRequest;
import ru.practicum.user.dto.UserDtoResponse;
import ru.practicum.user.dto.UserShortDtoResponse;
import ru.practicum.user.entity.User;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User mapDtoRequestToUser(UserDtoRequest dto);

    UserDtoResponse mapUserToDtoResponse(User user);

    UserShortDtoResponse mapUserToShortDtoResponse(User user);

    default Collection<UserDtoResponse> mapUsersToDtoResponses(Collection<User> users) {
        return users.stream().map(this::mapUserToDtoResponse).collect(toList());
    }
}
