package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ItemRequestMapperTest {

    private final User user = new User(1L, "user", "user@email.com");
    private final ItemRequest itemRequest = new ItemRequest(1L, "Need item", user, LocalDateTime.now());
    private final ItemRequestDto dto = new ItemRequestDto(1L, "Need item", 1L, itemRequest.getCreated(), List.of());
    private final NewRequest newRequest = new NewRequest("Need item", null);
    private final UpdateRequest updRequest = new UpdateRequest(1L, "Updated need", null, null);
    private final UpdateRequest updEmptyRequest = new UpdateRequest(1L, null, null, null);
    private final Item item = new Item(1L, "item", "description", true, user, 1L);

    @Test
    void toItemRequestDtoTest() {
        ItemRequestDto requestDto = ItemRequestMapper.INSTANCE.mapToItemRequestDto(itemRequest);
        assertThat(requestDto, equalTo(dto));
    }

    @Test
    void toItemRequestDtoWithItemsTest() {
        ItemRequestDto requestDto = ItemRequestMapper.INSTANCE.mapToItemRequestDto(itemRequest, List.of(item));
        assertThat(requestDto.getId(), equalTo(dto.getId()));
        assertThat(requestDto.getDescription(), equalTo(dto.getDescription()));
        assertThat(requestDto.getRequestorId(), equalTo(dto.getRequestorId()));
        assertThat(requestDto.getCreated(), equalTo(dto.getCreated()));
        assertThat(requestDto.getItems().size(), equalTo(1));
    }

    @Test
    void toItemRequestTest() {
        ItemRequest ir = ItemRequestMapper.INSTANCE.mapToItemRequest(newRequest, user, LocalDateTime.now());
        assertThat(ir.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(ir.getRequestor(), equalTo(user));
    }

    @Test
    void updateRequestFieldsTest() {
        ItemRequest ir = ItemRequestMapper.INSTANCE.updateRequestFromRequest(updRequest, itemRequest);
        assertThat(ir.getId(), equalTo(itemRequest.getId()));
        assertThat(ir.getDescription(), equalTo(updRequest.getDescription()));
        assertThat(ir.getRequestor(), equalTo(user));
        assertThat(ir.getCreated(), equalTo(itemRequest.getCreated()));
    }

    @Test
    void updateRequestEmptyFieldsTest() {
        ItemRequest ir = ItemRequestMapper.INSTANCE.updateRequestFromRequest(updEmptyRequest, itemRequest);
        assertThat(ir.getId(), equalTo(itemRequest.getId()));
        assertThat(ir.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(ir.getRequestor(), equalTo(user));
        assertThat(ir.getCreated(), equalTo(itemRequest.getCreated()));
    }
}