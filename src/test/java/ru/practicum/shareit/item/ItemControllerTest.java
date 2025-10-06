package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ExtendedItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mock;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExtendedItemService itemService;

    private final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final ItemDto itemDto = new ItemDto(
            1L,
            "item",
            "description",
            true,
            1L,
            null
    );
    private final UserDto userDto = new UserDto(1L, "user", "user@email.com");
    private final ExtendedItemDto extendedItemDto = new ExtendedItemDto(
            1L,
            "item",
            "description",
            true,
            1L,
            null,
            null,
            null,
            List.of());

    @Test
    public void testCreateItem() throws Exception {
        NewItemRequest request = new NewItemRequest(
                1L,
                "item",
                "description",
                true,
                1L,
                null
        );

        when(itemService.createItem(anyLong(), any(NewItemRequest.class)))
                .thenReturn(itemDto);

        mock.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("item"))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    public void testUpdateItem() throws Exception {
        UpdateItemRequest request = new UpdateItemRequest(
                1L,
                "updated",
                "updated desc",
                false,
                1L,
                null
        );
        ItemDto updatedItemDto = new ItemDto(1L, "updated", "updated desc", false, 1L, null);

        when(itemService.updateItem(anyLong(), any(UpdateItemRequest.class), anyLong()))
                .thenReturn(updatedItemDto);

        mock.perform(patch("/items/{id}", 1L)
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("updated"));
    }

    @Test
    public void testGetItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(extendedItemDto);

        mock.perform(get("/items/{id}", 1L)
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("item"));
    }

    @Test
    public void testGetAllItemsByUser() throws Exception {
        when(itemService.getAllItemsById(anyLong()))
                .thenReturn(List.of(extendedItemDto));

        mock.perform(get("/items")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testSearchItems() throws Exception {
        when(itemService.getItemByText(anyString()))
                .thenReturn(List.of(itemDto));

        mock.perform(get("/items/search")
                        .param("text", "item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("item"));
    }

    @Test
    public void testAddComment() throws Exception {
        NewCommentRequest request = new NewCommentRequest("comment", 1L, 1L);

        mock.perform(post("/items/{id}/comment", 1L)
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testDeleteItem() throws Exception {
        mock.perform(delete("/items/{id}", 1L)
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk());
    }
}
