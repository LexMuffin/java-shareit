package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class NewItemRequestJsonTest {

    @Autowired
    private JacksonTester<NewItemRequest> newItemRequestJson;

    @Test
    public void testNewItemRequestSerialization() throws IOException {
        NewItemRequest request = new NewItemRequest(1L, "name", "description", true, 1L, 5L);

        JsonContent<NewItemRequest> content = newItemRequestJson.write(request);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(content).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(content).extractingJsonPathNumberValue("$.owner").isEqualTo(1);
        assertThat(content).extractingJsonPathNumberValue("$.requestId").isEqualTo(5);
    }

    @Test
    public void testNewItemRequestDeserialization() throws IOException {
        String json = """
            {
                "id": 1,
                "name": "name",
                "description": "description",
                "available": true,
                "owner": 1,
                "requestId": 5
            }
            """;

        NewItemRequest request = newItemRequestJson.parseObject(json);

        assertThat(request).isNotNull();
        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getName()).isEqualTo("name");
        assertThat(request.getDescription()).isEqualTo("description");
        assertThat(request.getAvailable()).isTrue();
        assertThat(request.getOwner()).isEqualTo(1L);
        assertThat(request.getRequestId()).isEqualTo(5L);
    }

    @Test
    public void testNewItemRequestDeserializationWithRequiredFieldsOnly() throws IOException {
        String json = """
            {
                "name": "name",
                "description": "description",
                "available": true
            }
            """;

        NewItemRequest request = newItemRequestJson.parseObject(json);

        assertThat(request).isNotNull();
        assertThat(request.getName()).isEqualTo("name");
        assertThat(request.getDescription()).isEqualTo("description");
        assertThat(request.getAvailable()).isTrue();
        assertThat(request.getId()).isNull();
        assertThat(request.getOwner()).isNull();
        assertThat(request.getRequestId()).isNull();
    }
}