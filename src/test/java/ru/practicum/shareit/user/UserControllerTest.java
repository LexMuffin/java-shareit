package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mock;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private final UserDto userDto = new UserDto(1L, "user", "user@email.com");

    @Test
    public void testCreateUser() throws Exception {
        NewUserRequest request = new NewUserRequest("user", "user@email.com");

        when(userService.createUser(any(NewUserRequest.class)))
                .thenReturn(userDto);

        mock.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.email").value("user@email.com"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserDto updateUserDto = new UserDto(1L, "updated", "updated@email.com");

        UpdateUserRequest request = new UpdateUserRequest(1L, "updated", "updated@email.com");

        when(userService.updateUser(anyLong(), any(UpdateUserRequest.class)))
                .thenReturn(updateUserDto);

        mock.perform(patch("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testFindUser() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        mock.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    public void testGetUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(userDto));

        mock.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("user"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        mock.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());
    }

}
