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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest user) {
        return userService.createUser(user);
    }

    @GetMapping(UserControllerHeaderConstants.PATH)
    public UserDto findUser(@PathVariable(UserControllerHeaderConstants.ID) Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public Collection<UserDto> getUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping(UserControllerHeaderConstants.PATH)
    public UserDto update(@PathVariable(UserControllerHeaderConstants.ID) Long userId,
                          @Valid @RequestBody UpdateUserRequest newUser) {
        return userService.updateUser(userId, newUser);
    }

    @DeleteMapping(UserControllerHeaderConstants.PATH)
    public void delete(@PathVariable(UserControllerHeaderConstants.ID) Long userId) {
        userService.deleteUser(userId);
    }
}
