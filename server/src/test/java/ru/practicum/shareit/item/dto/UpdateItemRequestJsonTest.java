package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UpdateItemRequestJsonTest {

    @Autowired
    private JacksonTester<UpdateItemRequest> updateItemRequestJson;

    @Test
    public void testUpdateItemRequestSerialization() throws IOException {
        UpdateItemRequest request = new UpdateItemRequest(1L, "updatedName", "updatedDescription", false, 1L, 5L);

        JsonContent<UpdateItemRequest> content = updateItemRequestJson.write(request);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("updatedName");
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("updatedDescription");
        assertThat(content).extractingJsonPathBooleanValue("$.available").isEqualTo(false);
        assertThat(content).extractingJsonPathNumberValue("$.owner").isEqualTo(1);
        assertThat(content).extractingJsonPathNumberValue("$.requestId").isEqualTo(5);
    }

    @Test
    public void testUpdateItemRequestDeserialization() throws IOException {
        String json = "{" +
                "\"id\": 1," +
                "\"name\": \"updatedName\"," +
                "\"description\": \"updatedDescription\"," +
                "\"available\": false," +
                "\"owner\": 1," +
                "\"requestId\": 5" +
                "}";

        UpdateItemRequest request = updateItemRequestJson.parseObject(json);

        assertThat(request).isNotNull();
        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getName()).isEqualTo("updatedName");
        assertThat(request.getDescription()).isEqualTo("updatedDescription");
        assertThat(request.getAvailable()).isFalse();
        assertThat(request.getOwner()).isEqualTo(1L);
        assertThat(request.getRequestId()).isEqualTo(5L);
    }
}