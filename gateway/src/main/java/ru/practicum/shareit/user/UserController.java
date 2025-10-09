package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@Controller("gatewayUserController")
@Slf4j
@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserClient userClient;

    public final String path = "/{id}";
    public final String id = "id";

    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Valid @RequestBody NewUserRequest user) {
        return userClient.createUser(user);
    }

    @GetMapping(path)
    public ResponseEntity<Object> findUser(@PathVariable(id) Long userId) {
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getAllUsers();
    }

    @PatchMapping(path)
    public ResponseEntity<Object> update(@PathVariable(id) Long userId,
                          @Valid @RequestBody UpdateUserRequest newUser) {
        return userClient.updateUser(userId, newUser);
    }

    @DeleteMapping(path)
    public ResponseEntity<Object> delete(@PathVariable(id) Long userId) {
        return userClient.deleteUser(userId);
    }
}
