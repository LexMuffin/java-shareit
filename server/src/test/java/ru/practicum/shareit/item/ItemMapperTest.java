package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
class ItemMapperTest {

    private final User owner = new User(1L, "owner", "owner@email.com");
    private final Item item = new Item(1L, "item", "description", true, owner, null);
    private final ItemDto dto = new ItemDto(1L, "item", "description", true, 1L, null);
    private final NewItemRequest newItem = new NewItemRequest(null, "item", "description", true, null, null);
    private final UpdateItemRequest updItem = new UpdateItemRequest(1L, "updated", "updated desc", false, null, null);
    private final UpdateItemRequest updEmptyItem = new UpdateItemRequest(1L, null, null, null, null, null);
    private final Comment comment = new Comment(1L, "comment", item, owner, LocalDateTime.now());

    @Test
    void toItemDtoTest() {
        ItemDto itemDto = ItemMapper.INSTANCE.mapToItemDto(item);
        assertThat(itemDto, equalTo(dto));
    }

    @Test
    void toExtendedItemDtoTest() {
        ExtendedItemDto extendedDto = ItemMapper.INSTANCE.mapToExtendedItemDto(item, List.of(comment));
        assertThat(extendedDto.getId(), equalTo(item.getId()));
        assertThat(extendedDto.getName(), equalTo(item.getName()));
        assertThat(extendedDto.getDescription(), equalTo(item.getDescription()));
        assertThat(extendedDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(extendedDto.getComments().size(), equalTo(1));
    }

    @Test
    void toExtendedItemDtoWithBookingsTest() {
        LocalDateTime lastBooking = LocalDateTime.now().minusDays(1);
        LocalDateTime nextBooking = LocalDateTime.now().plusDays(1);
        ExtendedItemDto extendedDto = ItemMapper.INSTANCE.mapToExtendedItemDto(item, Optional.of(lastBooking), Optional.of(nextBooking), List.of(comment));
        assertThat(extendedDto.getId(), equalTo(item.getId()));
        assertThat(extendedDto.getLastBooking(), equalTo(lastBooking));
        assertThat(extendedDto.getNextBooking(), equalTo(nextBooking));
        assertThat(extendedDto.getComments().size(), equalTo(1));
    }

    @Test
    void toItemTest() {
        Item i = ItemMapper.INSTANCE.mapToItem(owner, newItem);
        assertThat(i.getName(), equalTo(item.getName()));
        assertThat(i.getDescription(), equalTo(item.getDescription()));
        assertThat(i.getAvailable(), equalTo(item.getAvailable()));
        assertThat(i.getOwner(), equalTo(owner));
    }

    @Test
    void updateItemFieldsTest() {
        Item i = ItemMapper.INSTANCE.updateItemFields(item, updItem);
        assertThat(i.getId(), equalTo(item.getId()));
        assertThat(i.getName(), equalTo(updItem.getName()));
        assertThat(i.getDescription(), equalTo(updItem.getDescription()));
        assertThat(i.getAvailable(), equalTo(updItem.getAvailable()));
        assertThat(i.getOwner(), equalTo(owner));
    }

    @Test
    void updateItemEmptyFieldsTest() {
        Item i = ItemMapper.INSTANCE.updateItemFields(item, updEmptyItem);
        assertThat(i.getId(), equalTo(item.getId()));
        assertThat(i.getName(), equalTo(item.getName()));
        assertThat(i.getDescription(), equalTo(item.getDescription()));
        assertThat(i.getAvailable(), equalTo(item.getAvailable()));
        assertThat(i.getOwner(), equalTo(owner));
    }
}