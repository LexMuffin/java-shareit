package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public final String PATH = "/{id}";
    public final String ID = "id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest user) {
        return userService.createUser(user);
    }

    @GetMapping(PATH)
    public UserDto findUser(@PathVariable(ID) Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public Collection<UserDto> getUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping(PATH)
    public UserDto update(@PathVariable(ID) Long userId,
                          @Valid @RequestBody UpdateUserRequest newUser) {
        return userService.updateUser(userId, newUser);
    }

    @DeleteMapping(PATH)
    public void delete(@PathVariable(ID) Long userId) {
        userService.deleteUser(userId);
    }
}
