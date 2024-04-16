package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.user.dto.UserDtoRequest;
import ru.practicum.user.entity.User;
import ru.practicum.user.mapping.UserMapper;
import ru.practicum.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Collection<User> findUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return userRepository.findByIdIn(ids, pageable).getContent();
    }

    @Override
    public User findUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%d was not found", userId)));
    }

    @Override
    public User createUser(UserDtoRequest dto) {
        User user = userMapper.mapDtoRequestToUser(dto);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%d was not found", userId)));
        userRepository.delete(user);
    }
}
