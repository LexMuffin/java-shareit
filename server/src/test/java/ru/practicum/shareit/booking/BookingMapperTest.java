package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingMapperTest {

    @Autowired
    private BookingMapper bookingMapper;

    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;
    private NewBookingRequest newBookingRequest;
    private UpdateBookingRequest updateBookingRequest;
    private UpdateBookingRequest emptyUpdateBookingRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@email.com");

        owner = new User();
        owner.setId(2L);
        owner.setName("owner");
        owner.setEmail("owner@email.com");

        item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequestId(null);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Statuses.WAITING);

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(Statuses.WAITING);

        newBookingRequest = new NewBookingRequest();
        newBookingRequest.setStart(booking.getStart());
        newBookingRequest.setEnd(booking.getEnd());
        newBookingRequest.setItemId(item.getId());

        updateBookingRequest = new UpdateBookingRequest();
        updateBookingRequest.setId(1L);
        updateBookingRequest.setStartDate(booking.getStart());
        updateBookingRequest.setEndDate(booking.getEnd());
        updateBookingRequest.setItemId(item.getId());
        updateBookingRequest.setBookerId(user.getId());
        updateBookingRequest.setStatus(Statuses.WAITING);

        emptyUpdateBookingRequest = new UpdateBookingRequest();
        emptyUpdateBookingRequest.setId(1L);
        emptyUpdateBookingRequest.setStartDate(null);
        emptyUpdateBookingRequest.setEndDate(null);
        emptyUpdateBookingRequest.setItemId(null);
        emptyUpdateBookingRequest.setBookerId(null);
        emptyUpdateBookingRequest.setStatus(null);
    }

    @Test
    void mapToBookingDto_shouldConvertBookingToBookingDto() {
        BookingDto result = bookingMapper.mapToBookingDto(booking);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
    }

    @Test
    void mapToBookingDto_shouldReturnNullWhenBookingIsNull() {
        BookingDto result = bookingMapper.mapToBookingDto(null);

        assertNull(result);
    }

    @Test
    void mapToBookingDto_shouldHandleBookingWithNullFields() {
        Booking emptyBooking = new Booking();
        emptyBooking.setId(null);
        emptyBooking.setStart(null);
        emptyBooking.setEnd(null);
        emptyBooking.setItem(null);
        emptyBooking.setBooker(null);
        emptyBooking.setStatus(null);

        assertThrows(NullPointerException.class, () -> {
            bookingMapper.mapToBookingDto(emptyBooking);
        });
    }

    @Test
    void mapToBooking_shouldConvertNewBookingRequestToBooking() {
        Booking result = bookingMapper.mapToBooking(newBookingRequest, user, item);

        assertNotNull(result);
        assertEquals(newBookingRequest.getStart(), result.getStart());
        assertEquals(newBookingRequest.getEnd(), result.getEnd());
        assertEquals(item, result.getItem());
        assertEquals(user, result.getBooker());
        assertEquals(Statuses.WAITING, result.getStatus());
        assertNull(result.getId());
    }

    @Test
    void mapToBooking_shouldReturnNullWhenAllParametersAreNull() {
        Booking result = bookingMapper.mapToBooking(null, null, null);

        assertNull(result);
    }

    @Test
    void mapToBooking_shouldHandleNullNewBookingRequest() {
        Booking result = bookingMapper.mapToBooking(null, user, item);

        assertNotNull(result);
        assertEquals(user, result.getBooker());
        assertEquals(item, result.getItem());
        assertNull(result.getStart());
        assertNull(result.getEnd());
        assertEquals(Statuses.WAITING, result.getStatus());
    }

    @Test
    void mapToBooking_shouldHandleNullUser() {
        Booking result = bookingMapper.mapToBooking(newBookingRequest, null, item);

        assertNotNull(result);
        assertEquals(newBookingRequest.getStart(), result.getStart());
        assertEquals(newBookingRequest.getEnd(), result.getEnd());
        assertEquals(item, result.getItem());
        assertNull(result.getBooker());
        assertEquals(Statuses.WAITING, result.getStatus());
    }

    @Test
    void mapToBooking_shouldHandleNullItem() {
        Booking result = bookingMapper.mapToBooking(newBookingRequest, user, null);

        assertNotNull(result);
        assertEquals(newBookingRequest.getStart(), result.getStart());
        assertEquals(newBookingRequest.getEnd(), result.getEnd());
        assertEquals(user, result.getBooker());
        assertNull(result.getItem());
        assertEquals(Statuses.WAITING, result.getStatus());
    }

    @Test
    void updateBookingFromRequest_shouldUpdateIdAndStatusFromUpdateBookingRequest() {
        LocalDateTime newStart = LocalDateTime.now().plusDays(2).withNano(0);
        LocalDateTime newEnd = LocalDateTime.now().plusDays(3).withNano(0);
        updateBookingRequest.setStartDate(newStart);
        updateBookingRequest.setEndDate(newEnd);
        updateBookingRequest.setStatus(Statuses.APPROVED);

        LocalDateTime originalStart = booking.getStart();
        LocalDateTime originalEnd = booking.getEnd();

        bookingMapper.updateBookingFromRequest(updateBookingRequest, booking);

        assertEquals(updateBookingRequest.getId(), booking.getId());
        assertEquals(Statuses.APPROVED, booking.getStatus());

        assertEquals(originalStart, booking.getStart());
        assertEquals(originalEnd, booking.getEnd());
    }

    @Test
    void updateBookingFromRequest_shouldHandleNullUpdateRequest() {
        try {
            bookingMapper.updateBookingFromRequest(null, booking);
            assertNotNull(booking);
        } catch (NullPointerException e) {
            fail("Не ожидалось NullPointerException: " + e.getMessage());
        }
    }

    @Test
    void updateBookingFromRequest_shouldHandleNullBooking() {
        assertThrows(NullPointerException.class, () -> {
            bookingMapper.updateBookingFromRequest(updateBookingRequest, null);
        });
    }

    @Test
    void updateBookingFromRequest_shouldNotUpdateFieldsWhenUpdateRequestHasNullValues() {
        LocalDateTime originalStart = booking.getStart();
        LocalDateTime originalEnd = booking.getEnd();
        Statuses originalStatus = booking.getStatus();

        bookingMapper.updateBookingFromRequest(emptyUpdateBookingRequest, booking);

        assertEquals(emptyUpdateBookingRequest.getId(), booking.getId());
        assertEquals(originalStart, booking.getStart());
        assertEquals(originalEnd, booking.getEnd());
        assertNull(booking.getStatus());
    }

    @Test
    void updateBookingFromRequest_shouldUpdateOnlyNonNullFields() {
        LocalDateTime originalStart = booking.getStart();
        LocalDateTime originalEnd = booking.getEnd();
        Statuses originalStatus = booking.getStatus();

        UpdateBookingRequest partialUpdate = new UpdateBookingRequest();
        partialUpdate.setStatus(Statuses.APPROVED);
        partialUpdate.setStartDate(null);
        partialUpdate.setEndDate(null);

        bookingMapper.updateBookingFromRequest(partialUpdate, booking);

        assertEquals(originalStart, booking.getStart());
        assertEquals(originalEnd, booking.getEnd());
        assertEquals(Statuses.APPROVED, booking.getStatus());
    }

    @Test
    void mapToBooking_shouldHandleEmptyAndNullValues() {
        NewBookingRequest emptyRequest = new NewBookingRequest();
        emptyRequest.setStart(null);
        emptyRequest.setEnd(null);
        emptyRequest.setItemId(null);

        Booking result = bookingMapper.mapToBooking(emptyRequest, user, item);

        assertNotNull(result);
        assertNull(result.getStart());
        assertNull(result.getEnd());
        assertEquals(item, result.getItem());
        assertEquals(user, result.getBooker());
        assertEquals(Statuses.WAITING, result.getStatus());
    }

    @Test
    void mapToBookingDto_shouldHandleMultipleConversions() {
        for (int i = 0; i < 100; i++) {
            Booking testBooking = new Booking();
            testBooking.setId((long) i);
            testBooking.setStart(LocalDateTime.now().plusDays(i));
            testBooking.setEnd(LocalDateTime.now().plusDays(i + 1));
            testBooking.setItem(item);
            testBooking.setBooker(user);
            testBooking.setStatus(i % 2 == 0 ? Statuses.APPROVED : Statuses.REJECTED);

            BookingDto result = bookingMapper.mapToBookingDto(testBooking);

            assertNotNull(result);
            assertEquals((long) i, result.getId());
            assertEquals(testBooking.getStart(), result.getStart());
            assertEquals(testBooking.getEnd(), result.getEnd());
            assertEquals(testBooking.getStatus(), result.getStatus());
        }
    }

    @Test
    void mapToBooking_shouldSetDefaultStatusWhenNotProvided() {
        NewBookingRequest requestWithoutStatus = new NewBookingRequest();
        requestWithoutStatus.setStart(LocalDateTime.now());
        requestWithoutStatus.setEnd(LocalDateTime.now().plusDays(1));
        requestWithoutStatus.setItemId(item.getId());

        Booking result = bookingMapper.mapToBooking(requestWithoutStatus, user, item);

        assertNotNull(result);
        assertEquals(Statuses.WAITING, result.getStatus());
    }
}