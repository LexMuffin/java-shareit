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

    public final String PATH = "/{id}";
    public final String ID = "id";

    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Valid @RequestBody NewUserRequest user) {
        return userClient.createUser(user);
    }

    @GetMapping(PATH)
    public ResponseEntity<Object> findUser(@PathVariable(ID) Long userId) {
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getAllUsers();
    }

    @PatchMapping(PATH)
    public ResponseEntity<Object> update(@PathVariable(ID) Long userId,
                          @Valid @RequestBody UpdateUserRequest newUser) {
        return userClient.updateUser(userId, newUser);
    }

    @DeleteMapping(PATH)
    public ResponseEntity<Object> delete(@PathVariable(ID) Long userId) {
        return userClient.deleteUser(userId);
    }
}
