package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    public void testUserDtoSerialization() throws Exception {
        UserDto userDto = new UserDto(1L, "name", "email@email.com");

        JsonContent<UserDto> content = json.write(userDto);

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
        UserDto userDto = json.parseObject(stringJson);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("name");
        assertThat(userDto.getEmail()).isEqualTo("email@email.com");
    }
}
