package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDtoRequest;
import ru.practicum.user.dto.UserDtoResponse;
import ru.practicum.user.mapping.UserMapper;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * @param ids id пользователей
     * @param from количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size количество элементов в наборе
     * Получение информации о пользователях.
     * Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки), либо о конкретных (учитываются указанные идентификаторы)
     * В случае, если по заданным фильтрам не найдено ни одного пользователя, возвращает пустой список
     */
    @GetMapping
    public Collection<UserDtoResponse> getUsers(@RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Пришел GET-запрос main-service/admin/users?ids={}&from={}&size={} без тела", ids, from, size);
        Collection<UserDtoResponse> dtos = userMapper.mapUsersToDtoResponses(
                userService.findUsers(ids, from, size)
        );
        log.info("Ответ на GET-запрос main-service/admin/users?ids={}&from={}&size={} с телом={}", ids, from, size, dtos);
        return dtos;
    }

    /**
     * @param dto данные добавляемого пользователя
     * Добавление нового пользователя
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDtoResponse createUser(@Valid @RequestBody UserDtoRequest dto) {
        log.info("Пришел POST-запрос main-service/admin/users с телом={}", dto);
        UserDtoResponse savedDto = userMapper.mapUserToDtoResponse(userService.createUser(dto));
        log.info("Ответ на POST-запрос main-service/admin/users с телом={}", savedDto);
        return savedDto;
    }

    /**
     * @param userId id пользователя
     * Удаление пользователя
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Пришел DELETE-запрос main-service/admin/users/{userId={}} без тела", userId);
        userService.deleteUser(userId);
        log.info("Ответ на DELETE-запрос main-service/admin/users/{userId={}} без тела", userId);
    }
}
