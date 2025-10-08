package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> userDtoJson;

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
    public void testUserDtoDeserializationWithNullValues() throws IOException {
        String stringJson = "{" +
                "\"id\": null," +
                "\"name\": null," +
                "\"email\": null" +
                "}";
        UserDto userDto = userDtoJson.parseObject(stringJson);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isNull();
        assertThat(userDto.getName()).isNull();
        assertThat(userDto.getEmail()).isNull();
    }

    @Test
    public void testUserDtoDeserializationWithPartialData() throws IOException {
        String stringJson = "{" +
                "\"name\": \"name\"," +
                "\"email\": \"email@email.com\"" +
                "}";
        UserDto userDto = userDtoJson.parseObject(stringJson);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isNull();
        assertThat(userDto.getName()).isEqualTo("name");
        assertThat(userDto.getEmail()).isEqualTo("email@email.com");
    }
}