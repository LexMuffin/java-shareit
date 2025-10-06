package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.enums.Statuses;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> bookingDtoJson;

    private final UserDto userDto = new UserDto(1L, "user", "user@email.com");
    private final ItemDto itemDto = new ItemDto(1L, "item", "description", true, 1L, null);

    @Test
    public void testBookingDtoSerialization() throws IOException {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 2, 10, 0);
        BookingDto bookingDto = new BookingDto(1L, start, end, itemDto, userDto, Statuses.WAITING);

        JsonContent<BookingDto> content = bookingDtoJson.write(bookingDto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.start").isEqualTo("2024-01-01T10:00:00");
        assertThat(content).extractingJsonPathStringValue("$.end").isEqualTo("2024-01-02T10:00:00");
        assertThat(content).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");

        assertThat(content).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(content).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.booker.name").isEqualTo("user");
    }

    @Test
    public void testBookingDtoDeserialization() throws IOException {
        String json = """
            {
                "id": 1,
                "start": "2024-01-01T10:00:00",
                "end": "2024-01-02T10:00:00",
                "status": "APPROVED",
                "item": {
                    "id": 1,
                    "name": "item",
                    "description": "description",
                    "available": true,
                    "ownerId": 1,
                    "requestId": null
                },
                "booker": {
                    "id": 1,
                    "name": "user",
                    "email": "user@email.com"
                }
            }
            """;

        BookingDto bookingDto = bookingDtoJson.parseObject(json);

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getId()).isEqualTo(1L);
        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
        assertThat(bookingDto.getStatus()).isEqualTo(Statuses.APPROVED);
        assertThat(bookingDto.getItem().getId()).isEqualTo(1L);
        assertThat(bookingDto.getItem().getName()).isEqualTo("item");
        assertThat(bookingDto.getBooker().getId()).isEqualTo(1L);
        assertThat(bookingDto.getBooker().getName()).isEqualTo("user");
    }
}
