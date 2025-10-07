package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id %d не найден", userId)));
    }

    private User isUserEmailExists(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ConflictException(String.format("Пользователь с почтой %s существует", email)));
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest request) {
        log.info("POST /users - добавление нового пользователя");
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.error("Пользователь с почтой \"{}\" уже существует", request.getEmail());
            throw new ConflictException("Пользователь с такой почтой уже существует");
        }
        User user = UserMapper.mapToUser(request);
        User newUser = userRepository.save(user);

        return UserMapper.mapToUserDto(newUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UpdateUserRequest request) {
        log.info("PUT /users - обновление существующего пользователя");
        if (userId == null) {
            log.error("Идентификатор пользователя должен быть указан");
            throw new NotFoundException("Пользователь не указан");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.error("Пользователь с почтой \"{}\" уже существует", request.getEmail());
            throw new ConflictException("Пользователь с такой почтой уже существует");
        }
        User updatedUser = UserMapper.updateUserFields(findUser(userId), request);
        userRepository.save(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("DELETE /users - удаление существующего пользователя");
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        log.info("GET /users - получение существующего пользователя");
        return UserMapper.mapToUserDto(findUser(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }
}
