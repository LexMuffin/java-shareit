package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> userDtoJson;

    @Autowired
    private JacksonTester<NewUserRequest> newUserRequestJson;

    @Autowired
    private JacksonTester<UpdateUserRequest> updateUserRequestJson;

    @Test
    public void testUserDtoSerialization() throws Exception {
        UserDto userDto = new UserDto(1L, "name", "email@email.com");

        JsonContent<UserDto> content = userDtoJson.write(userDto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId().intValue());
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(content).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }

    @Test
    public void testUserDtoDeserialization() throws IOException {
        String stringJson = "{" +
                "\"id\": 1," +
                "\"name\": \"name\"," +
                "\"email\": \"email@email.com\"" +
                "}";
        UserDto userDto = userDtoJson.parseObject(stringJson);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("name");
        assertThat(userDto.getEmail()).isEqualTo("email@email.com");
    }

    @Test
    public void testNewUserRequestSerialization() throws IOException {
        NewUserRequest newUserRequest = new NewUserRequest("John Doe", "john.doe@example.com");

        JsonContent<NewUserRequest> content = newUserRequestJson.write(newUserRequest);

        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(content).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@example.com");
    }

    @Test
    public void testNewUserRequestDeserialization() throws IOException {
        String json = "{" +
                "\"name\": \"John Doe\"," +
                "\"email\": \"john.doe@example.com\"" +
                "}";

        NewUserRequest newUserRequest = newUserRequestJson.parseObject(json);

        assertThat(newUserRequest).isNotNull();
        assertThat(newUserRequest.getName()).isEqualTo("John Doe");
        assertThat(newUserRequest.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    public void testUpdateUserRequestSerialization() throws IOException {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(1L, "Updated Name", "updated@example.com");

        JsonContent<UpdateUserRequest> content = updateUserRequestJson.write(updateUserRequest);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("Updated Name");
        assertThat(content).extractingJsonPathStringValue("$.email").isEqualTo("updated@example.com");
    }

    @Test
    public void testUpdateUserRequestDeserialization() throws IOException {
        String json = "{" +
                "\"id\": 1," +
                "\"name\": \"Updated Name\"," +
                "\"email\": \"updated@example.com\"" +
                "}";

        UpdateUserRequest updateUserRequest = updateUserRequestJson.parseObject(json);

        assertThat(updateUserRequest).isNotNull();
        assertThat(updateUserRequest.getId()).isEqualTo(1L);
        assertThat(updateUserRequest.getName()).isEqualTo("Updated Name");
        assertThat(updateUserRequest.getEmail()).isEqualTo("updated@example.com");
    }
}
