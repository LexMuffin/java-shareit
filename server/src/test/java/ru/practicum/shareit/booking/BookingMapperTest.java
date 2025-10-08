package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Statuses;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
class BookingMapperTest {

    private final User user = new User(1L, "user", "user@email.com");
    private final User owner = new User(2L, "owner", "owner@email.com");
    private final Item item = new Item(1L, "item", "description", true, owner, null);
    private final Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, Statuses.WAITING);
    private final BookingDto dto = new BookingDto(1L, booking.getStart(), booking.getEnd(), null, null, Statuses.WAITING);
    private final NewBookingRequest newBooking = new NewBookingRequest(booking.getStart(), booking.getEnd(), item.getId(), null);
    private final UpdateBookingRequest updBooking = new UpdateBookingRequest(1L, booking.getStart(), booking.getEnd(), item.getId(), user.getId(), Statuses.WAITING);
    private final UpdateBookingRequest updEmptyBooking = new UpdateBookingRequest(1L, null, null, null, null, null);

    @Test
    void toBookingDtoTest() {
        BookingDto bookingDto = BookingMapper.INSTANCE.mapToBookingDto(booking);
        assertThat(bookingDto.getId(), equalTo(dto.getId()));
        assertThat(bookingDto.getStart(), equalTo(dto.getStart()));
        assertThat(bookingDto.getEnd(), equalTo(dto.getEnd()));
        assertThat(bookingDto.getStatus(), equalTo(dto.getStatus()));
    }

    @Test
    void toBookingTest() {
        Booking b = BookingMapper.INSTANCE.mapToBooking(newBooking, user, item);
        assertThat(b.getStart(), equalTo(booking.getStart()));
        assertThat(b.getEnd(), equalTo(booking.getEnd()));
        assertThat(b.getStatus(), equalTo(booking.getStatus()));
        assertThat(b.getItem(), equalTo(item));
        assertThat(b.getBooker(), equalTo(user));
    }

    @Test
    void updateBookingFieldsTest() {
        BookingMapper.INSTANCE.updateBookingFromRequest(updBooking, booking);
        assertThat(updBooking.getId(), equalTo(booking.getId()));
        assertThat(updBooking.getStartDate(), equalTo(booking.getStart()));
        assertThat(updBooking.getEndDate(), equalTo(booking.getEnd()));
        assertThat(updBooking.getStatus(), equalTo(booking.getStatus()));
    }

    @Test
    void updateBookingEmptyFieldsTest() {
        BookingMapper.INSTANCE.updateBookingFromRequest(updEmptyBooking, booking);
        assertThat(updBooking.getId(), equalTo(booking.getId()));
        assertThat(updBooking.getStartDate(), equalTo(booking.getStart()));
        assertThat(updBooking.getEndDate(), equalTo(booking.getEnd()));
    }
}