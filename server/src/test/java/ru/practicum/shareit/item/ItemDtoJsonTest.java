package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> itemDtoJson;

    @Autowired
    private JacksonTester<CommentDto> commentDtoJson;

    @Autowired
    private JacksonTester<ExtendedItemDto> extendedItemDtoJson;

    @Autowired
    private JacksonTester<NewItemRequest> newItemRequestJson;

    @Autowired
    private JacksonTester<UpdateItemRequest> updateItemRequestJson;

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
        String json = "{" +
                "\"id\": 1," +
                "\"name\": \"name\"," +
                "\"description\": \"description\"," +
                "\"available\": true," +
                "\"owner\": 1," +
                "\"requestId\": null" +
                "}";

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
        String json = "{" +
                "\"id\": 1," +
                "\"text\": \"comment\"," +
                "\"authorName\": \"authorName\"," +
                "\"created\": \"2024-01-01T10:00:00\"" +
                "}";

        CommentDto commentDto = commentDtoJson.parseObject(json);

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("comment");
        assertThat(commentDto.getAuthorName()).isEqualTo("authorName");
        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0, 0));
    }

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
        String json = "{" +
                "\"id\": 1," +
                "\"name\": \"name\"," +
                "\"description\": \"description\"," +
                "\"available\": true," +
                "\"owner\": 1," +
                "\"requestId\": 5," +
                "\"lastBooking\": \"2024-01-01T10:00:00\"," +
                "\"nextBooking\": \"2024-01-02T10:00:00\"," +
                "\"comments\": [" +
                "   {" +
                "       \"id\": 1," +
                "       \"text\": \"Great item!\"," +
                "       \"authorName\": \"user1\"," +
                "       \"created\": \"2024-01-01T12:00:00\"" +
                "   }" +
                "]" +
                "}";

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
        String json = "{" +
                "\"id\": 1," +
                "\"name\": \"name\"," +
                "\"description\": \"description\"," +
                "\"available\": true," +
                "\"owner\": 1," +
                "\"requestId\": 5" +
                "}";

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
