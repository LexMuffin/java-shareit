package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ExtendedItemDtoJsonTest {

    @Autowired
    private JacksonTester<ExtendedItemDto> extendedItemDtoJson;

    @Test
    public void testExtendedItemDtoSerialization() throws IOException {
        LocalDateTime lastBooking = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime nextBooking = LocalDateTime.of(2024, 1, 2, 10, 0);
        List<CommentDto> comments = List.of(
                new CommentDto(1L, "Great item!", 1L, "user1", LocalDateTime.of(2024, 1, 1, 12, 0)),
                new CommentDto(2L, "Nice quality", 2L, "user2", LocalDateTime.of(2024, 1, 2, 14, 0))
        );

        ExtendedItemDto extendedItemDto = new ExtendedItemDto(
                1L, "name", "description", true, 1L, null,
                lastBooking, nextBooking, comments
        );

        JsonContent<ExtendedItemDto> content = extendedItemDtoJson.write(extendedItemDto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(content).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(content).extractingJsonPathNumberValue("$.owner").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.requestId").isNull();
        assertThat(content).extractingJsonPathStringValue("$.lastBooking").isEqualTo("2024-01-01T10:00:00");
        assertThat(content).extractingJsonPathStringValue("$.nextBooking").isEqualTo("2024-01-02T10:00:00");

        assertThat(content).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Great item!");
        assertThat(content).extractingJsonPathStringValue("$.comments[1].text").isEqualTo("Nice quality");
    }

    @Test
    public void testExtendedItemDtoDeserialization() throws IOException {
        String json = """
            {
                "id": 1,
                "name": "name",
                "description": "description",
                "available": true,
                "owner": 1,
                "requestId": 5,
                "lastBooking": "2024-01-01T10:00:00",
                "nextBooking": "2024-01-02T10:00:00",
                "comments": [
                    {
                        "id": 1,
                        "text": "Great item!",
                        "authorName": "user1",
                        "created": "2024-01-01T12:00:00"
                    }
                ]
            }
            """;

        ExtendedItemDto extendedItemDto = extendedItemDtoJson.parseObject(json);

        assertThat(extendedItemDto).isNotNull();
        assertThat(extendedItemDto.getId()).isEqualTo(1L);
        assertThat(extendedItemDto.getName()).isEqualTo("name");
        assertThat(extendedItemDto.getDescription()).isEqualTo("description");
        assertThat(extendedItemDto.getAvailable()).isTrue();
        assertThat(extendedItemDto.getOwner()).isEqualTo(1L);
        assertThat(extendedItemDto.getRequestId()).isEqualTo(5L);
        assertThat(extendedItemDto.getLastBooking()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(extendedItemDto.getNextBooking()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
        assertThat(extendedItemDto.getComments()).hasSize(1);
        assertThat(extendedItemDto.getComments().get(0).getText()).isEqualTo("Great item!");
    }

    @Test
    public void testExtendedItemDtoSerializationWithNullValues() throws IOException {
        ExtendedItemDto extendedItemDto = new ExtendedItemDto(
                1L, "name", "description", false, 1L, 5L,
                null, null, null
        );

        JsonContent<ExtendedItemDto> content = extendedItemDtoJson.write(extendedItemDto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(content).extractingJsonPathBooleanValue("$.available").isEqualTo(false);
        assertThat(content).extractingJsonPathNumberValue("$.requestId").isEqualTo(5);
        assertThat(content).extractingJsonPathStringValue("$.lastBooking").isNull();
        assertThat(content).extractingJsonPathStringValue("$.nextBooking").isNull();
        assertThat(content).extractingJsonPathArrayValue("$.comments").isNull();
    }
}