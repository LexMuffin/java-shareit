package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserMapperTest {

    private UserMapper userMapper;

    private User user;
    private UserDto userDto;
    private NewUserRequest newUserRequest;
    private UpdateUserRequest updateUserRequest;
    private UpdateUserRequest emptyUpdateUserRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@email.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("user");
        userDto.setEmail("user@email.com");

        newUserRequest = new NewUserRequest();
        newUserRequest.setName("user");
        newUserRequest.setEmail("user@email.com");

        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setId(1L);
        updateUserRequest.setName("updated");
        updateUserRequest.setEmail("updated@email.com");

        emptyUpdateUserRequest = new UpdateUserRequest();
        emptyUpdateUserRequest.setId(1L);
        emptyUpdateUserRequest.setName(null);
        emptyUpdateUserRequest.setEmail(null);
    }

    @Test
    void mapToUserDto_shouldConvertUserToUserDto() {
        UserDto result = userMapper.mapToUserDto(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void mapToUserDto_shouldHandleUserWithNullFields() {
        User emptyUser = new User();
        emptyUser.setId(null);
        emptyUser.setName(null);
        emptyUser.setEmail(null);

        UserDto result = userMapper.mapToUserDto(emptyUser);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getEmail());
    }

    @Test
    void mapToUser_shouldConvertNewUserRequestToUser() {
        User result = userMapper.mapToUser(newUserRequest);

        assertNotNull(result);
        assertEquals(newUserRequest.getName(), result.getName());
        assertEquals(newUserRequest.getEmail(), result.getEmail());
        assertNull(result.getId());
    }

    @Test
    void mapToUser_shouldHandleNewUserRequestWithNullFields() {
        NewUserRequest emptyRequest = new NewUserRequest();
        emptyRequest.setName(null);
        emptyRequest.setEmail(null);

        User result = userMapper.mapToUser(emptyRequest);

        assertNotNull(result);
        assertNull(result.getName());
        assertNull(result.getEmail());
        assertNull(result.getId());
    }

    @Test
    void updateUserFields_shouldUpdateAllFieldsFromUpdateUserRequest() {
        User result = userMapper.updateUserFields(user, updateUserRequest);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(updateUserRequest.getName(), result.getName());
        assertEquals(updateUserRequest.getEmail(), result.getEmail());
    }

    @Test
    void updateUserFields_shouldReturnNullWhenAllParametersAreNull() {
        assertThrows(NullPointerException.class, () -> {
            userMapper.updateUserFields(null, null);
        });
    }

    @Test
    void updateUserFields_shouldThrowExceptionWhenUserIsNull() {
        assertThrows(NullPointerException.class, () -> {
            userMapper.updateUserFields(null, updateUserRequest);
        });
    }

    @Test
    void updateUserFields_shouldThrowExceptionWhenUpdateRequestIsNull() {
        assertThrows(NullPointerException.class, () -> {
            userMapper.updateUserFields(user, null);
        });
    }

    @Test
    void updateUserFields_shouldNotUpdateFieldsWhenUpdateRequestHasNullValues() {
        User result = userMapper.updateUserFields(user, emptyUpdateUserRequest);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void updateUserFields_shouldUpdateOnlyNonNullFields() {
        UpdateUserRequest partialUpdate = new UpdateUserRequest();
        partialUpdate.setId(1L);
        partialUpdate.setName("updated name only");
        partialUpdate.setEmail(null);

        User result = userMapper.updateUserFields(user, partialUpdate);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals("updated name only", result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void updateUserFields_shouldUpdateOnlyEmailWhenNameIsNull() {
        UpdateUserRequest emailOnlyUpdate = new UpdateUserRequest();
        emailOnlyUpdate.setId(1L);
        emailOnlyUpdate.setName(null);
        emailOnlyUpdate.setEmail("updated@email.com");

        User result = userMapper.updateUserFields(user, emailOnlyUpdate);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals("updated@email.com", result.getEmail());
    }

    @Test
    void mapToUser_shouldHandleEmptyStrings() {
        NewUserRequest emptyStringsRequest = new NewUserRequest();
        emptyStringsRequest.setName("");
        emptyStringsRequest.setEmail("");

        User result = userMapper.mapToUser(emptyStringsRequest);

        assertNotNull(result);
        assertEquals("", result.getName());
        assertEquals("", result.getEmail());
        assertNull(result.getId());
    }

    @Test
    void mapToUserDto_shouldHandleMultipleConversions() {
        for (int i = 0; i < 100; i++) {
            User testUser = new User();
            testUser.setId((long) i);
            testUser.setName("User " + i);
            testUser.setEmail("user" + i + "@email.com");

            UserDto result = userMapper.mapToUserDto(testUser);

            assertNotNull(result);
            assertEquals((long) i, result.getId());
            assertEquals("User " + i, result.getName());
            assertEquals("user" + i + "@email.com", result.getEmail());
        }
    }

    @Test
    void mapToUser_shouldHandleMultipleConversions() {
        for (int i = 0; i < 100; i++) {
            NewUserRequest testRequest = new NewUserRequest();
            testRequest.setName("New User " + i);
            testRequest.setEmail("newuser" + i + "@email.com");

            User result = userMapper.mapToUser(testRequest);

            assertNotNull(result);
            assertEquals("New User " + i, result.getName());
            assertEquals("newuser" + i + "@email.com", result.getEmail());
            assertNull(result.getId());
        }
    }

    @Test
    void updateUserFields_shouldNotUpdateWhenEmptyStrings() {
        UpdateUserRequest emptyStringsUpdate = new UpdateUserRequest();
        emptyStringsUpdate.setId(1L);
        emptyStringsUpdate.setName("");
        emptyStringsUpdate.setEmail("");

        String originalName = user.getName();
        String originalEmail = user.getEmail();

        User result = userMapper.updateUserFields(user, emptyStringsUpdate);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(originalName, result.getName());
        assertEquals(originalEmail, result.getEmail());
    }
}