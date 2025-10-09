package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemMapperTest {

    @Autowired
    private ItemMapper itemMapper;

    private User owner;
    private Item item;
    private ItemDto itemDto;
    private NewItemRequest newItemRequest;
    private UpdateItemRequest updateItemRequest;
    private UpdateItemRequest emptyUpdateItemRequest;
    private Comment comment;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("owner");
        owner.setEmail("owner@email.com");

        item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequestId(null);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("item");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setOwner(1L);
        itemDto.setRequestId(null);

        newItemRequest = new NewItemRequest();
        newItemRequest.setName("item");
        newItemRequest.setDescription("description");
        newItemRequest.setAvailable(true);

        updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setId(1L);
        updateItemRequest.setName("updated");
        updateItemRequest.setDescription("updated desc");
        updateItemRequest.setAvailable(false);

        emptyUpdateItemRequest = new UpdateItemRequest();
        emptyUpdateItemRequest.setId(1L);
        emptyUpdateItemRequest.setName(null);
        emptyUpdateItemRequest.setDescription(null);
        emptyUpdateItemRequest.setAvailable(null);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("comment");
        comment.setItem(item);
        comment.setAuthor(owner);
        comment.setCreated(LocalDateTime.now());
    }

    @Test
    void mapToItemDto_shouldConvertItemToItemDto() {
        ItemDto result = itemMapper.mapToItemDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(owner.getId(), result.getOwner());
    }

    @Test
    void mapToItemDto_shouldReturnNullWhenItemIsNull() {
        ItemDto result = itemMapper.mapToItemDto(null);

        assertNull(result);
    }

    @Test
    void mapToItemDto_shouldHandleNullOwner() {
        item.setOwner(null);

        ItemDto result = itemMapper.mapToItemDto(item);

        assertNotNull(result);
        assertNull(result.getOwner());
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
    }

    @Test
    void mapToExtendedItemDto_shouldConvertItemToExtendedItemDtoWithComments() {
        ExtendedItemDto result = itemMapper.mapToExtendedItemDto(item, List.of(comment));

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertNotNull(result.getComments());
        assertEquals(1, result.getComments().size());
        assertEquals(comment.getText(), result.getComments().get(0).getText());
    }

    @Test
    void mapToExtendedItemDto_shouldReturnNullWhenItemIsNull() {
        ExtendedItemDto result = itemMapper.mapToExtendedItemDto(null, List.of(comment));

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getDescription());
        assertNull(result.getOwner());
        assertNotNull(result.getComments());
        assertEquals(1, result.getComments().size());
    }

    @Test
    void mapToExtendedItemDto_shouldHandleNullComments() {
        ExtendedItemDto result = itemMapper.mapToExtendedItemDto(item, null);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertNull(result.getComments());
    }

    @Test
    void mapToExtendedItemDto_shouldConvertItemToExtendedItemDtoWithBookingsAndComments() {
        LocalDateTime lastBooking = LocalDateTime.now().minusDays(1);
        LocalDateTime nextBooking = LocalDateTime.now().plusDays(1);

        ExtendedItemDto result = itemMapper.mapToExtendedItemDto(
                item,
                Optional.of(lastBooking),
                Optional.of(nextBooking),
                List.of(comment)
        );

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(lastBooking, result.getLastBooking());
        assertEquals(nextBooking, result.getNextBooking());
        assertNotNull(result.getComments());
        assertEquals(1, result.getComments().size());
    }

    @Test
    void mapToExtendedItemDto_shouldHandleEmptyOptionalBookings() {
        ExtendedItemDto result = itemMapper.mapToExtendedItemDto(
                item,
                Optional.empty(),
                Optional.empty(),
                List.of(comment)
        );

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertNotNull(result.getComments());
        assertEquals(1, result.getComments().size());
    }

    @Test
    void mapToItem_shouldConvertNewItemRequestToItem() {
        Item result = itemMapper.mapToItem(owner, newItemRequest);

        assertNotNull(result);
        assertEquals(newItemRequest.getName(), result.getName());
        assertEquals(newItemRequest.getDescription(), result.getDescription());
        assertEquals(newItemRequest.getAvailable(), result.getAvailable());
        assertEquals(owner, result.getOwner());
        assertNull(result.getId());
    }

    @Test
    void mapToItem_shouldReturnNullWhenAllParametersAreNull() {
        Item result = itemMapper.mapToItem(null, null);

        assertNull(result);
    }

    @Test
    void mapToItem_shouldHandleNullNewItemRequest() {
        Item result = itemMapper.mapToItem(owner, null);

        assertNotNull(result);
        assertEquals(owner, result.getOwner());
        assertNull(result.getName());
        assertNull(result.getDescription());
        assertNull(result.getAvailable());
    }

    @Test
    void mapToItem_shouldHandleNullOwner() {
        Item result = itemMapper.mapToItem(null, newItemRequest);

        assertNotNull(result);
        assertEquals(newItemRequest.getName(), result.getName());
        assertEquals(newItemRequest.getDescription(), result.getDescription());
        assertEquals(newItemRequest.getAvailable(), result.getAvailable());
        assertNull(result.getOwner());
    }

    @Test
    void updateItemFields_shouldUpdateAllFieldsFromUpdateItemRequest() {
        Item result = itemMapper.updateItemFields(item, updateItemRequest);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(updateItemRequest.getName(), result.getName());
        assertEquals(updateItemRequest.getDescription(), result.getDescription());
        assertEquals(updateItemRequest.getAvailable(), result.getAvailable());
        assertEquals(owner, result.getOwner());
    }

    @Test
    void updateItemFields_shouldThrowExceptionWhenAllParametersAreNull() {
        assertThrows(NullPointerException.class, () -> {
            itemMapper.updateItemFields(null, null);
        });
    }

    @Test
    void updateItemFields_shouldThrowExceptionWhenUpdateRequestIsNull() {
        assertThrows(NullPointerException.class, () -> {
            itemMapper.updateItemFields(item, null);
        });
    }

    @Test
    void updateItemFields_shouldNotUpdateFieldsWhenUpdateRequestHasNullValues() {
        Item result = itemMapper.updateItemFields(item, emptyUpdateItemRequest);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(owner, result.getOwner());
    }

    @Test
    void updateItemFields_shouldUpdateOnlyNonNullFields() {
        UpdateItemRequest partialUpdate = new UpdateItemRequest();
        partialUpdate.setId(1L);
        partialUpdate.setName("updated name only");
        partialUpdate.setDescription(null);
        partialUpdate.setAvailable(null);

        Item result = itemMapper.updateItemFields(item, partialUpdate);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals("updated name only", result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(owner, result.getOwner());
    }

    @Test
    void mapToItem_shouldHandleEmptyAndNullValues() {
        NewItemRequest emptyRequest = new NewItemRequest();
        emptyRequest.setName("");
        emptyRequest.setDescription("");
        emptyRequest.setAvailable(null);

        Item result = itemMapper.mapToItem(owner, emptyRequest);

        assertNotNull(result);
        assertEquals("", result.getName());
        assertEquals("", result.getDescription());
        assertNull(result.getAvailable());
        assertEquals(owner, result.getOwner());
    }

    @Test
    void mapToItemDto_shouldHandleMultipleConversions() {
        for (int i = 0; i < 100; i++) {
            Item testItem = new Item();
            testItem.setId((long) i);
            testItem.setName("Item " + i);
            testItem.setDescription("Description " + i);
            testItem.setAvailable(i % 2 == 0);
            testItem.setOwner(owner);

            ItemDto result = itemMapper.mapToItemDto(testItem);

            assertNotNull(result);
            assertEquals((long) i, result.getId());
            assertEquals("Item " + i, result.getName());
            assertEquals("Description " + i, result.getDescription());
            assertEquals(i % 2 == 0, result.getAvailable());
            assertEquals(owner.getId(), result.getOwner());
        }
    }
}