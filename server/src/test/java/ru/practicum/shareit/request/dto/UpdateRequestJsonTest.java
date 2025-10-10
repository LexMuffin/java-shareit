package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UpdateRequestJsonTest {

    @Autowired
    private JacksonTester<UpdateRequest> updateRequestJson;

    @Test
    public void testUpdateRequestSerialization() throws IOException {
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        UpdateRequest updateRequest = new UpdateRequest(1L, "Обновленное описание", 1L, created);

        JsonContent<UpdateRequest> content = updateRequestJson.write(updateRequest);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("Обновленное описание");
        assertThat(content).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.created").isEqualTo("2024-01-01T10:00:00");
    }

    @Test
    public void testUpdateRequestDeserialization() throws IOException {
        // CHECKSTYLE:OFF
        String json = """
            {
                "id": 1,
                "description": "Обновленное описание",
                "requestorId": 1,
                "created": "2024-01-01T10:00:00"
            }
            """;
        // CHECKSTYLE:ON

        UpdateRequest updateRequest = updateRequestJson.parseObject(json);

        assertThat(updateRequest).isNotNull();
        assertThat(updateRequest.getId()).isEqualTo(1L);
        assertThat(updateRequest.getDescription()).isEqualTo("Обновленное описание");
        assertThat(updateRequest.getRequestorId()).isEqualTo(1L);
        assertThat(updateRequest.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0, 0));
    }

    @Test
    public void testUpdateRequestSerializationWithNullValues() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(null, null, null, null);

        JsonContent<UpdateRequest> content = updateRequestJson.write(updateRequest);

        assertThat(content).extractingJsonPathStringValue("$.id").isNull();
        assertThat(content).extractingJsonPathStringValue("$.description").isNull();
        assertThat(content).extractingJsonPathStringValue("$.requestorId").isNull();
        assertThat(content).extractingJsonPathStringValue("$.created").isNull();
    }

    @Test
    public void testUpdateRequestDeserializationWithPartialData() throws IOException {
        // CHECKSTYLE:OFF
        String json = """
            {
                "description": "Только описание"
            }
            """;
        // CHECKSTYLE:ON

        UpdateRequest updateRequest = updateRequestJson.parseObject(json);

        assertThat(updateRequest).isNotNull();
        assertThat(updateRequest.getId()).isNull();
        assertThat(updateRequest.getDescription()).isEqualTo("Только описание");
        assertThat(updateRequest.getRequestorId()).isNull();
        assertThat(updateRequest.getCreated()).isNull();
    }

    @Test
    public void testUpdateRequestDeserializationWithOnlyId() throws IOException {
        // CHECKSTYLE:OFF
        String json = """
            {
                "id": 5
            }
            """;
        // CHECKSTYLE:ON

        UpdateRequest updateRequest = updateRequestJson.parseObject(json);

        assertThat(updateRequest).isNotNull();
        assertThat(updateRequest.getId()).isEqualTo(5L);
        assertThat(updateRequest.getDescription()).isNull();
        assertThat(updateRequest.getRequestorId()).isNull();
        assertThat(updateRequest.getCreated()).isNull();
    }
}