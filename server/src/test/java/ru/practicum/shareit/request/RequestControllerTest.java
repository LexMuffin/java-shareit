package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class RequestControllerTest {

    @Autowired
    private MockMvc mock;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService requestService;

    private final String xSharerUserId = "X-Sharer-User-Id";

    private final UserDto userDto = new UserDto(1L, "user", "user@email.com");
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "description",
            userDto.getId(),
            LocalDateTime.now(),
            List.of()
    );

    @Test
    public void testCreateItemRequest() throws Exception {
        NewRequest request = new NewRequest("description", 1L);

        when(requestService.createItemRequest(anyLong(), any(NewRequest.class)))
                .thenReturn(itemRequestDto);

        mock.perform(post("/requests")
                        .header(xSharerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    public void testFindItemRequest() throws Exception {
        when(requestService.findItemRequest(anyLong()))
                .thenReturn(itemRequestDto);

        mock.perform(get("/requests/{id}", 1L)
                        .header(xSharerUserId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.requestorId").value(1L));
    }

    @Test
    public void testFindAllByRequestorId() throws Exception {
        when(requestService.findAllByRequestorId(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mock.perform(get("/requests")
                        .header(xSharerUserId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].requestorId").value(1L));
    }

    @Test
    public void testFindAllOfAnotherRequestors() throws Exception {
        when(requestService.findAllOfAnotherRequestors(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mock.perform(get("/requests/all")
                        .header(xSharerUserId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testDeleteRequest() throws Exception {
        mock.perform(delete("/requests/{id}", 1L)
                        .header(xSharerUserId, 1L))
                .andExpect(status().isOk());
    }
}
