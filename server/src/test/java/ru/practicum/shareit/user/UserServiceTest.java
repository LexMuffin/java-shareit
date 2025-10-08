package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserWhenValidDataShouldCreateUser() {
        NewUserRequest request = new NewUserRequest("user", "user@email.com");
        User user = new User(1L, "user", "user@email.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("user");
        assertThat(result.getEmail()).isEqualTo("user@email.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUserWhenEmailExistsShouldThrowException() {
        NewUserRequest request = new NewUserRequest("user", "user@email.com");
        User existingUser = new User(1L, "existing", "user@email.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Пользователь с такой почтой уже существует");
    }

    @Test
    void updateUserWhenValidDataShouldUpdateUser() {
        Long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest(userId, "updated", "updated@email.com");
        User existingUser = new User(userId, "old", "old@email.com");
        User updatedUser = new User(userId, "updated", "updated@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(userId, request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("updated");
        assertThat(result.getEmail()).isEqualTo("updated@email.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserWhenUserIdIsNullShouldThrowException() {
        Long userId = null;
        UpdateUserRequest request = new UpdateUserRequest(null, "updated", "updated@email.com");

        assertThatThrownBy(() -> userService.updateUser(userId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не указан");
    }

    @Test
    void updateUserWhenUserNotFoundShouldThrowException() {
        Long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest(userId, "updated", "updated@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь c id 1 не найден");
    }

    @Test
    void deleteUserWhenValidDataShouldDeleteUser() {
        Long userId = 1L;

        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void getUserByIdWhenValidDataShouldReturnUser() {
        Long userId = 1L;
        User user = new User(userId, "user", "user@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo("user");
    }

    @Test
    void getUserByIdWhenUserNotFoundShouldThrowException() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь c id 1 не найден");
    }

    @Test
    void getAllUsersWhenUsersExistShouldReturnUsers() {
        User user1 = new User(1L, "user1", "user1@email.com");
        User user2 = new User(2L, "user2", "user2@email.com");
        List<User> users = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        Collection<UserDto> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.stream().map(UserDto::getName)).containsExactly("user1", "user2");
    }

    @Test
    void getAllUsersWhenNoUsersShouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        Collection<UserDto> result = userService.getAllUsers();

        assertThat(result).isEmpty();
    }


    @Test
    void createUserWhenEmailExistsWithDifferentUserShouldThrowException() {
        NewUserRequest request = new NewUserRequest("newuser", "existing@email.com");
        User existingUser = new User(1L, "existing", "existing@email.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Пользователь с такой почтой уже существует");
    }
}