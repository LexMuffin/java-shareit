package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto createUser(NewUserRequest request) {
        log.info("POST /users - добавление нового пользователя");

        if (userStorage.isUserEmailExists(request.getEmail())) {
            log.error("Пользователь с почтой \"{}\" уже существует", request.getEmail());
            throw new ConflictException("Пользователь с такой почтой уже существует");
        }
        User user = UserMapper.mapToUser(request);
        userStorage.createUser(user);

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UpdateUserRequest request) {
        log.info("PUT /users - обновление существующего пользователя");
        if (userId == null) {
            log.error("Пользователь с идентификатором - \"{}\" не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        if (userStorage.isUserEmailExists(request.getEmail())) {
            log.error("Пользователь с почтой \"{}\" уже существует", request.getEmail());
            throw new ConflictException("Пользователь с такой почтой уже существует");
        }
        User updatedUser = UserMapper.updateUserFields(userStorage.getUserById(userId), request);
        userStorage.updateUser(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("DELETE /users - удаление существующего пользователя");
        userStorage.deleteUser(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.info("GET /users - получение существующего пользователя");
        if (userId == null) {
            log.error("Пользователь с идентификатором - \"{}\" не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        return UserMapper.mapToUserDto(userStorage.getUserById(userId));
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }
}
