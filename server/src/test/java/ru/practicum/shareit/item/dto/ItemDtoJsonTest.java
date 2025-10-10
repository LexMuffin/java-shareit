package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> itemDtoJson;

    @Test
    public void testItemDtoSerialization() throws IOException {
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, 1L, null);

        JsonContent<ItemDto> content = itemDtoJson.write(itemDto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(content).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(content).extractingJsonPathNumberValue("$.owner").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.requestId").isNull();
    }

    @Test
    public void testItemDtoDeserialization() throws IOException {
        String json = """
            {
                "id": 1,
                "name": "name",
                "description": "description",
                "available": true,
                "owner": 1,
                "requestId": null
            }
            """;

        ItemDto itemDto = itemDtoJson.parseObject(json);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("name");
        assertThat(itemDto.getDescription()).isEqualTo("description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getOwner()).isEqualTo(1L);
        assertThat(itemDto.getRequestId()).isNull();
    }

    @Test
    public void testItemDtoDeserializationWithRequestId() throws IOException {
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

        ItemDto itemDto = itemDtoJson.parseObject(json);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("name");
        assertThat(itemDto.getRequestId()).isEqualTo(5L);
    }
}