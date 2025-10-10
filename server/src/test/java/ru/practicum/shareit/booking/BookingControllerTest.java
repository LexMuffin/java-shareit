package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.Statuses;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mock;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private final String xSharerUserId = "X-Sharer-User-Id";

    private final UserDto userDto = new UserDto(1L, "user", "user@email.com");
    private final ItemDto itemDto = new ItemDto(1L, "item", "description", true, 1L, null);
    private final BookingDto bookingDto = new BookingDto(1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            itemDto,
            userDto,
            Statuses.WAITING
    );

    @Test
    public void testCreateBooking() throws Exception {
        NewBookingRequest newBookingRequest = new NewBookingRequest(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1L,
                1L
        );

        when(bookingService.createBooking(anyLong(), any(NewBookingRequest.class)))
                .thenReturn(bookingDto);

        mock.perform(post("/bookings")
                .header(xSharerUserId, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBookingRequest)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.booker.id").value(1L));
    }

    @Test
    public void testUpdateBooking() throws Exception {
        BookingDto updatedBookingDto = new BookingDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                itemDto,
                userDto,
                Statuses.APPROVED
        );

        UpdateBookingRequest updateBookingRequest = new UpdateBookingRequest(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1L,
                1L,
                Statuses.APPROVED
        );

        when(bookingService.updateBooking(anyLong(), any(UpdateBookingRequest.class)))
                .thenReturn(updatedBookingDto);

        mock.perform(put("/bookings")
                .header(xSharerUserId, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateBookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    public void testFindBooking() throws Exception {
        when(bookingService.findBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mock.perform(get("/bookings/{id}", 1L)
                .header(xSharerUserId, 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    public void testFindAllBookingsByUser() throws Exception {
        when(bookingService.findAllBookingsByUser(anyLong(), anyString()))
                .thenReturn(List.of(bookingDto));

        mock.perform(get("/bookings")
                        .header(xSharerUserId, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].booker.id").value(1L));
    }

    @Test
    public void testFindAllBookingsByOwnerItems() throws Exception {
        when(bookingService.findAllBookingsByOwnerItems(anyLong(), anyString()))
                .thenReturn(List.of(bookingDto));

        mock.perform(get("/bookings/owner")
                        .header(xSharerUserId, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].item.id").value(1L));
    }

    @Test
    public void testDeleteBooking() throws Exception {
        mock.perform(delete("/bookings/{id}", 1L)
                        .header(xSharerUserId, 1L))
                .andExpect(status().isOk());
    }
}
