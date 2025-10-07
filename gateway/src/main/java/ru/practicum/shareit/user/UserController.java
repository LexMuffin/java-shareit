package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserControllerHeaderConstants;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@Controller("gatewayUserController")
@Slf4j
@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserClient userClient;

    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Valid @RequestBody NewUserRequest user) {
        return userClient.createUser(user);
    }

    @GetMapping(UserControllerHeaderConstants.PATH)
    public ResponseEntity<Object> findUser(@PathVariable(UserControllerHeaderConstants.ID) Long userId) {
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getAllUsers();
    }

    @PatchMapping(UserControllerHeaderConstants.PATH)
    public ResponseEntity<Object> update(@PathVariable(UserControllerHeaderConstants.ID) Long userId,
                          @Valid @RequestBody UpdateUserRequest newUser) {
        return userClient.updateUser(userId, newUser);
    }

    @DeleteMapping(UserControllerHeaderConstants.PATH)
    public ResponseEntity<Object> delete(@PathVariable(UserControllerHeaderConstants.ID) Long userId) {
        return userClient.deleteUser(userId);
    }
}
