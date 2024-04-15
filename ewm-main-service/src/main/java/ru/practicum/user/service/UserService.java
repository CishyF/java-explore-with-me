package ru.practicum.user.service;

import ru.practicum.user.dto.UserDtoRequest;
import ru.practicum.user.entity.User;

import java.util.Collection;
import java.util.List;

public interface UserService {

    Collection<User> findUsers(List<Long> ids, int from, int size);

    User findUser(long userId);

    User createUser(UserDtoRequest dto);

    void deleteUser(long userId);
}
