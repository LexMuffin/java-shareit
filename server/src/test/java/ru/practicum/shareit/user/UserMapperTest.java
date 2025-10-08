package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class UserMapperTest {

    private final User user = new User(1L, "user", "user@email.com");
    private final UserDto dto = new UserDto(1L, "user", "user@email.com");
    private final NewUserRequest newUser = new NewUserRequest("user", "user@email.com");
    private final UpdateUserRequest updUser = new UpdateUserRequest(1L, "updated", "updated@email.com");
    private final UpdateUserRequest updEmptyUser = new UpdateUserRequest(1L, null, null);

    @Test
    void toUserDtoTest() {
        UserDto userDto = UserMapper.mapToUserDto(user);
        assertThat(userDto, equalTo(dto));
    }

    @Test
    void toUserTest() {
        User u = UserMapper.mapToUser(newUser);
        assertThat(u.getName(), equalTo(user.getName()));
        assertThat(u.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void updateUserFieldsTest() {
        User u = UserMapper.updateUserFields(user, updUser);
        assertThat(u.getId(), equalTo(user.getId()));
        assertThat(u.getName(), equalTo(updUser.getName()));
        assertThat(u.getEmail(), equalTo(updUser.getEmail()));
    }

    @Test
    void updateUserEmptyFieldsTest() {
        User u = UserMapper.updateUserFields(user, updEmptyUser);
        assertThat(u.getId(), equalTo(user.getId()));
        assertThat(u.getName(), equalTo(user.getName()));
        assertThat(u.getEmail(), equalTo(user.getEmail()));
    }
}