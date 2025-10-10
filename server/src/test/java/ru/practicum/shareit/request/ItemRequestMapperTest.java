package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRequestMapperTest {

    @Autowired
    private ItemRequestMapper itemRequestMapper;

    private User requestor;
    private User owner;
    private Item item;
    private ItemRequest itemRequest;
    private NewRequest newRequest;
    private UpdateRequest updateRequest;
    private LocalDateTime created;

    @BeforeEach
    void setUp() {
        created = LocalDateTime.now().withNano(0);

        requestor = new User();
        requestor.setId(1L);
        requestor.setName("requestor");
        requestor.setEmail("requestor@email.com");

        owner = new User();
        owner.setId(2L);
        owner.setName("owner");
        owner.setEmail("owner@email.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequestId(null);

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Need item for testing");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(created);

        newRequest = new NewRequest();
        newRequest.setDescription("Need item for testing");

        updateRequest = new UpdateRequest();
        updateRequest.setDescription("Updated description for item request");
    }

    @Test
    void mapToResponseDto_shouldConvertItemToResponseDto() {
        ResponseDto result = itemRequestMapper.mapToResponseDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(owner.getId(), result.getOwnerId());
    }

    @Test
    void mapToResponseDto_shouldReturnNullWhenItemIsNull() {
        ResponseDto result = itemRequestMapper.mapToResponseDto(null);

        assertNull(result);
    }

    @Test
    void mapToResponseDto_shouldHandleItemWithNullOwner() {
        item.setOwner(null);

        ResponseDto result = itemRequestMapper.mapToResponseDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertNull(result.getOwnerId());
    }

    @Test
    void mapToResponseDto_shouldHandleItemWithNullOwnerId() {
        owner.setId(null);
        item.setOwner(owner);

        ResponseDto result = itemRequestMapper.mapToResponseDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertNull(result.getOwnerId());
    }

    @Test
    void mapToItemRequestDto_shouldConvertItemRequestToItemRequestDtoWithEmptyItems() {
        ItemRequestDto result = itemRequestMapper.mapToItemRequestDto(itemRequest);

        assertNotNull(result);
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(requestor.getId(), result.getRequestorId());
        assertEquals(itemRequest.getCreated(), result.getCreated());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void mapToItemRequestDto_shouldReturnNullWhenItemRequestIsNull() {
        ItemRequestDto result = itemRequestMapper.mapToItemRequestDto(null);

        assertNull(result);
    }

    @Test
    void mapToItemRequestDto_shouldHandleItemRequestWithNullRequestor() {
        itemRequest.setRequestor(null);

        ItemRequestDto result = itemRequestMapper.mapToItemRequestDto(itemRequest);

        assertNotNull(result);
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertNull(result.getRequestorId());
        assertEquals(itemRequest.getCreated(), result.getCreated());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void mapToItemRequestDto_shouldHandleItemRequestWithNullRequestorId() {
        requestor.setId(null);
        itemRequest.setRequestor(requestor);

        ItemRequestDto result = itemRequestMapper.mapToItemRequestDto(itemRequest);

        assertNotNull(result);
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertNull(result.getRequestorId());
        assertEquals(itemRequest.getCreated(), result.getCreated());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void mapToItemRequestDto_shouldConvertItemRequestToItemRequestDtoWithItems() {
        List<Item> items = Arrays.asList(item);

        ItemRequestDto result = itemRequestMapper.mapToItemRequestDto(itemRequest, items);

        assertNotNull(result);
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(requestor.getId(), result.getRequestorId());
        assertEquals(itemRequest.getCreated(), result.getCreated());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals(item.getId(), result.getItems().get(0).getId());
        assertEquals(item.getName(), result.getItems().get(0).getName());
    }

    @Test
    void mapToItemRequestDto_shouldHandleNullItemsCollection() {
        ItemRequestDto result = itemRequestMapper.mapToItemRequestDto(itemRequest, null);

        assertNotNull(result);
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(requestor.getId(), result.getRequestorId());
        assertEquals(itemRequest.getCreated(), result.getCreated());
        assertNull(result.getItems());
    }

    @Test
    void mapToItemRequestDto_shouldHandleEmptyItemsCollection() {
        ItemRequestDto result = itemRequestMapper.mapToItemRequestDto(itemRequest, Collections.emptyList());

        assertNotNull(result);
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(requestor.getId(), result.getRequestorId());
        assertEquals(itemRequest.getCreated(), result.getCreated());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void mapToItemRequest_shouldConvertNewRequestToItemRequest() {
        ItemRequest result = itemRequestMapper.mapToItemRequest(newRequest, requestor, created);

        assertNotNull(result);
        assertEquals(newRequest.getDescription(), result.getDescription());
        assertEquals(requestor, result.getRequestor());
        assertEquals(created, result.getCreated());
        assertNotNull(result);
    }

    @Test
    void mapToItemRequest_shouldReturnNullWhenAllParametersAreNull() {
        ItemRequest result = itemRequestMapper.mapToItemRequest(null, null, null);

        assertNull(result);
    }

    @Test
    void mapToItemRequest_shouldHandleNullNewRequest() {
        ItemRequest result = itemRequestMapper.mapToItemRequest(null, requestor, created);

        assertNotNull(result);
        assertEquals(requestor, result.getRequestor());
        assertEquals(created, result.getCreated());
        assertNull(result.getDescription());
    }

    @Test
    void mapToItemRequest_shouldHandleNullUser() {
        ItemRequest result = itemRequestMapper.mapToItemRequest(newRequest, null, created);

        assertNotNull(result);
        assertEquals(newRequest.getDescription(), result.getDescription());
        assertEquals(created, result.getCreated());
        assertNull(result.getRequestor());
    }

    @Test
    void mapToItemRequest_shouldHandleNullDateTime() {
        ItemRequest result = itemRequestMapper.mapToItemRequest(newRequest, requestor, null);

        assertNotNull(result);
        assertEquals(newRequest.getDescription(), result.getDescription());
        assertEquals(requestor, result.getRequestor());
        assertNull(result.getCreated());
    }

    @Test
    void mapToResponseDtoList_shouldConvertItemListToResponseDtoList() {
        List<Item> items = Arrays.asList(item);

        List<ResponseDto> result = itemRequestMapper.mapToResponseDtoList(items);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(owner.getId(), result.get(0).getOwnerId());
    }

    @Test
    void mapToResponseDtoList_shouldReturnNullWhenItemListIsNull() {
        List<ResponseDto> result = itemRequestMapper.mapToResponseDtoList(null);

        assertNull(result);
    }

    @Test
    void mapToResponseDtoList_shouldHandleEmptyItemList() {
        List<ResponseDto> result = itemRequestMapper.mapToResponseDtoList(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void updateRequestFromRequest_shouldUpdateItemRequestDescription() {
        String originalDescription = itemRequest.getDescription();

        itemRequestMapper.updateRequestFromRequest(updateRequest, itemRequest);

        assertEquals(updateRequest.getDescription(), itemRequest.getDescription());
    }

    @Test
    void updateRequestFromRequest_shouldNotUpdateWhenUpdateRequestIsNull() {
        String originalDescription = itemRequest.getDescription();

        itemRequestMapper.updateRequestFromRequest(null, itemRequest);

        assertEquals(originalDescription, itemRequest.getDescription());
    }

    @Test
    void updateRequestFromRequest_shouldThrowExceptionWhenItemRequestIsNull() {
        assertThrows(NullPointerException.class, () -> {
            itemRequestMapper.updateRequestFromRequest(updateRequest, null);
        });
    }

    @Test
    void updateRequestFromRequest_shouldHandleNullDescriptionInUpdateRequest() {
        String originalDescription = itemRequest.getDescription();
        updateRequest.setDescription(null);

        itemRequestMapper.updateRequestFromRequest(updateRequest, itemRequest);

        assertEquals(originalDescription, itemRequest.getDescription());
    }

    @Test
    void updateRequestFromRequest_shouldHandleEmptyDescriptionInUpdateRequest() {
        updateRequest.setDescription("");

        itemRequestMapper.updateRequestFromRequest(updateRequest, itemRequest);

        assertEquals("", itemRequest.getDescription());
    }

    @Test
    void mapToResponseDto_shouldHandleMultipleConversions() {
        for (int i = 0; i < 100; i++) {
            Item testItem = new Item();
            testItem.setId((long) i);
            testItem.setName("Item " + i);
            testItem.setOwner(owner);

            ResponseDto result = itemRequestMapper.mapToResponseDto(testItem);

            assertNotNull(result);
            assertEquals((long) i, result.getId());
            assertEquals("Item " + i, result.getName());
            assertEquals(owner.getId(), result.getOwnerId());
        }
    }

    @Test
    void mapToItemRequestDto_shouldHandleItemRequestWithNullCreated() {
        itemRequest.setCreated(null);

        ItemRequestDto result = itemRequestMapper.mapToItemRequestDto(itemRequest);

        assertNotNull(result);
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(requestor.getId(), result.getRequestorId());
        assertNull(result.getCreated());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void mapToItemRequest_shouldHandleNewRequestWithNullDescription() {
        newRequest.setDescription(null);

        ItemRequest result = itemRequestMapper.mapToItemRequest(newRequest, requestor, created);

        assertNotNull(result);
        assertNull(result.getDescription());
        assertEquals(requestor, result.getRequestor());
        assertEquals(created, result.getCreated());
    }
}