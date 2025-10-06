package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> itemDtoJson;

    @Autowired
    private JacksonTester<CommentDto> commentDtoJson;

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
    public void testCommentDtoSerialization() throws IOException {

        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        CommentDto commentDto = new CommentDto(1L, "comment", 1L, "authorName", created);

        JsonContent<CommentDto> content = commentDtoJson.write(commentDto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.text").isEqualTo("comment");
        assertThat(content).extractingJsonPathStringValue("$.authorName").isEqualTo("authorName");
        assertThat(content).extractingJsonPathStringValue("$.created").isEqualTo("2024-01-01T10:00:00");
    }

    @Test
    public void testCommentDtoDeserialization() throws IOException {
        String json = """
            {
                "id": 1,
                "text": "comment",
                "authorName": "authorName",
                "created": "2024-01-01T10:00:00"
            }
            """;

        CommentDto commentDto = commentDtoJson.parseObject(json);

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("comment");
        assertThat(commentDto.getAuthorName()).isEqualTo("authorName");
        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0, 0));
    }
}
