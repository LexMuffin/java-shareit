package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class NewUserRequestJsonTest {

    @Autowired
    private JacksonTester<NewUserRequest> newUserRequestJson;

    @Test
    public void testNewUserRequestSerialization() throws IOException {
        NewUserRequest newUserRequest = new NewUserRequest("John Doe", "john.doe@example.com");

        JsonContent<NewUserRequest> content = newUserRequestJson.write(newUserRequest);

        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(content).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@example.com");
    }

    @Test
    public void testNewUserRequestDeserialization() throws IOException {
        // CHECKSTYLE:OFF
        String json = """
            {
                "name": "John Doe",
                "email": "john.doe@example.com"
            }
            """;
        // CHECKSTYLE:ON

        NewUserRequest newUserRequest = newUserRequestJson.parseObject(json);

        assertThat(newUserRequest).isNotNull();
        assertThat(newUserRequest.getName()).isEqualTo("John Doe");
        assertThat(newUserRequest.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    public void testNewUserRequestDeserializationWithEmptyName() throws IOException {
        // CHECKSTYLE:OFF
        String json = """
            {
                "name": "",
                "email": "john.doe@example.com"
            }
            """;
        // CHECKSTYLE:ON

        NewUserRequest newUserRequest = newUserRequestJson.parseObject(json);

        assertThat(newUserRequest).isNotNull();
        assertThat(newUserRequest.getName()).isEmpty();
        assertThat(newUserRequest.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    public void testNewUserRequestDeserializationWithWhitespaceName() throws IOException {
        // CHECKSTYLE:OFF
        String json = """
            {
                "name": "   ",
                "email": "john.doe@example.com"
            }
            """;
        // CHECKSTYLE:ON

        NewUserRequest newUserRequest = newUserRequestJson.parseObject(json);

        assertThat(newUserRequest).isNotNull();
        assertThat(newUserRequest.getName()).isEqualTo("   ");
        assertThat(newUserRequest.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    public void testNewUserRequestDeserializationWithEmptyEmail() throws IOException {
        // CHECKSTYLE:OFF
        String json = """
            {
                "name": "John Doe",
                "email": ""
            }
            """;
        // CHECKSTYLE:ON

        NewUserRequest newUserRequest = newUserRequestJson.parseObject(json);

        assertThat(newUserRequest).isNotNull();
        assertThat(newUserRequest.getName()).isEqualTo("John Doe");
        assertThat(newUserRequest.getEmail()).isEmpty();
    }

    @Test
    public void testNewUserRequestDeserializationWithOnlyName() throws IOException {
        // CHECKSTYLE:OFF
        String json = """
            {
                "name": "John Doe"
            }
            """;
        // CHECKSTYLE:ON

        NewUserRequest newUserRequest = newUserRequestJson.parseObject(json);

        assertThat(newUserRequest).isNotNull();
        assertThat(newUserRequest.getName()).isEqualTo("John Doe");
        assertThat(newUserRequest.getEmail()).isNull();
    }

    @Test
    public void testNewUserRequestDeserializationWithOnlyEmail() throws IOException {
        // CHECKSTYLE:OFF
        String json = """
            {
                "email": "john.doe@example.com"
            }
            """;
        // CHECKSTYLE:ON

        NewUserRequest newUserRequest = newUserRequestJson.parseObject(json);

        assertThat(newUserRequest).isNotNull();
        assertThat(newUserRequest.getName()).isNull();
        assertThat(newUserRequest.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    public void testNewUserRequestDeserializationWithEmptyObject() throws IOException {
        String json = "{}";

        NewUserRequest newUserRequest = newUserRequestJson.parseObject(json);

        assertThat(newUserRequest).isNotNull();
        assertThat(newUserRequest.getName()).isNull();
        assertThat(newUserRequest.getEmail()).isNull();
    }
}