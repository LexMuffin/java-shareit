package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UpdateUserRequestJsonTest {

    @Autowired
    private JacksonTester<UpdateUserRequest> updateUserRequestJson;

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

    @Test
    public void testUpdateUserRequestSerializationWithNullValues() throws IOException {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(null, null, null);

        JsonContent<UpdateUserRequest> content = updateUserRequestJson.write(updateUserRequest);

        assertThat(content).extractingJsonPathStringValue("$.id").isNull();
        assertThat(content).extractingJsonPathStringValue("$.name").isNull();
        assertThat(content).extractingJsonPathStringValue("$.email").isNull();
    }

    @Test
    public void testUpdateUserRequestDeserializationWithPartialData() throws IOException {
        String json = "{" +
                "\"id\": 1," +
                "\"name\": \"Updated Name\"" +
                "}";

        UpdateUserRequest updateUserRequest = updateUserRequestJson.parseObject(json);

        assertThat(updateUserRequest).isNotNull();
        assertThat(updateUserRequest.getId()).isEqualTo(1L);
        assertThat(updateUserRequest.getName()).isEqualTo("Updated Name");
        assertThat(updateUserRequest.getEmail()).isNull();
    }

    @Test
    public void testUpdateUserRequestDeserializationWithOnlyEmail() throws IOException {
        String json = "{" +
                "\"email\": \"updated@example.com\"" +
                "}";

        UpdateUserRequest updateUserRequest = updateUserRequestJson.parseObject(json);

        assertThat(updateUserRequest).isNotNull();
        assertThat(updateUserRequest.getId()).isNull();
        assertThat(updateUserRequest.getName()).isNull();
        assertThat(updateUserRequest.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    public void testUpdateUserRequestDeserializationWithEmptyObject() throws IOException {
        String json = "{}";

        UpdateUserRequest updateUserRequest = updateUserRequestJson.parseObject(json);

        assertThat(updateUserRequest).isNotNull();
        assertThat(updateUserRequest.getId()).isNull();
        assertThat(updateUserRequest.getName()).isNull();
        assertThat(updateUserRequest.getEmail()).isNull();
    }

    @Test
    public void testUpdateUserRequestHelperMethods() throws IOException {
        String json = "{" +
                "\"id\": 1," +
                "\"name\": \"Updated Name\"," +
                "\"email\": \"updated@example.com\"" +
                "}";

        UpdateUserRequest updateUserRequest = updateUserRequestJson.parseObject(json);

        assertThat(updateUserRequest).isNotNull();
        assertThat(updateUserRequest.hasName()).isTrue();
        assertThat(updateUserRequest.hasEmail()).isTrue();

        UpdateUserRequest requestWithEmptyStrings = new UpdateUserRequest(1L, " ", " ");
        assertThat(requestWithEmptyStrings.hasName()).isFalse();
        assertThat(requestWithEmptyStrings.hasEmail()).isFalse();

        UpdateUserRequest requestWithNulls = new UpdateUserRequest(1L, null, null);
        assertThat(requestWithNulls.hasName()).isFalse();
        assertThat(requestWithNulls.hasEmail()).isFalse();

        UpdateUserRequest requestWithWhitespaceEmail = new UpdateUserRequest(1L, "Name", "   ");
        assertThat(requestWithWhitespaceEmail.hasName()).isTrue();
        assertThat(requestWithWhitespaceEmail.hasEmail()).isFalse();
    }

    @Test
    public void testUpdateUserRequestDeserializationWithEmptyStrings() throws IOException {
        String json = "{" +
                "\"id\": 1," +
                "\"name\": \"\"," +
                "\"email\": \"\"" +
                "}";

        UpdateUserRequest updateUserRequest = updateUserRequestJson.parseObject(json);

        assertThat(updateUserRequest).isNotNull();
        assertThat(updateUserRequest.getId()).isEqualTo(1L);
        assertThat(updateUserRequest.getName()).isEmpty();
        assertThat(updateUserRequest.getEmail()).isEmpty();
        assertThat(updateUserRequest.hasName()).isFalse();
        assertThat(updateUserRequest.hasEmail()).isFalse();
    }

    @Test
    public void testUpdateUserRequestDeserializationWithWhitespaceStrings() throws IOException {
        String json = "{" +
                "\"id\": 1," +
                "\"name\": \"   \"," +
                "\"email\": \"   \"" +
                "}";

        UpdateUserRequest updateUserRequest = updateUserRequestJson.parseObject(json);

        assertThat(updateUserRequest).isNotNull();
        assertThat(updateUserRequest.getId()).isEqualTo(1L);
        assertThat(updateUserRequest.getName()).isEqualTo("   ");
        assertThat(updateUserRequest.getEmail()).isEqualTo("   ");
        assertThat(updateUserRequest.hasName()).isFalse();
        assertThat(updateUserRequest.hasEmail()).isFalse();
    }
}