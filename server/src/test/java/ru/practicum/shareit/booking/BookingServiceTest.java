package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.enums.Statuses;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotItemOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBookingWhenValidDataShouldCreateBooking() {
        Long userId = 1L;
        Long itemId = 1L;
        User user = new User(userId, "user", "user@email.com");
        User owner = new User(2L, "owner", "owner@email.com");
        Item item = new Item(itemId, "item", "description", true, owner, null);
        NewBookingRequest request = new NewBookingRequest(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), itemId, null);
        Booking booking = new Booking(1L, request.getStart(), request.getEnd(), item, user, Statuses.WAITING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.createBooking(userId, request);

        assertThat(result).isNotNull();
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBookingWhenUserNotFoundShouldThrowException() {
        Long userId = 1L;
        NewBookingRequest request = new NewBookingRequest(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 1L, null);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(userId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь c id 1 не найден");
    }

    @Test
    void createBookingWhenItemNotFoundShouldThrowException() {
        Long userId = 1L;
        Long itemId = 1L;
        User user = new User(userId, "user", "user@email.com");
        NewBookingRequest request = new NewBookingRequest(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), itemId, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(userId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь c id 1 не найдена");
    }

    @Test
    void createBookingWhenItemNotAvailableShouldThrowException() {
        Long userId = 1L;
        Long itemId = 1L;
        User user = new User(userId, "user", "user@email.com");
        User owner = new User(2L, "owner", "owner@email.com");
        Item item = new Item(itemId, "item", "description", false, owner, null);
        NewBookingRequest request = new NewBookingRequest(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), itemId, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.createBooking(userId, request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Вещь не доступна для бронирования!");
    }

    @Test
    void createBookingWhenOwnerBooksOwnItemShouldThrowException() {
        Long userId = 1L;
        Long itemId = 1L;
        User user = new User(userId, "user", "user@email.com");
        Item item = new Item(itemId, "item", "description", true, user, null);
        NewBookingRequest request = new NewBookingRequest(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), itemId, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.createBooking(userId, request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Владелец вещи не может забронировать свою же вещь");
    }

    @Test
    void findBookingWhenValidDataShouldReturnBooking() {
        Long bookingId = 1L;
        Long userId = 1L;
        User user = new User(userId, "user", "user@email.com");
        User owner = new User(2L, "owner", "owner@email.com");
        Item item = new Item(1L, "item", "description", true, owner, null);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, Statuses.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        BookingDto result = bookingService.findBooking(bookingId, userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(bookingId);
    }

    @Test
    void findBookingWhenBookingNotFoundShouldThrowException() {
        Long bookingId = 1L;
        Long userId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> bookingService.findBooking(bookingId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Бронирование c id 1 не найдено");
    }

    @Test
    void findBookingWhenUserNotAuthorizedShouldThrowException() {
        Long bookingId = 1L;
        Long userId = 3L;
        User user = new User(1L, "user", "user@email.com");
        User owner = new User(2L, "owner", "owner@email.com");
        Item item = new Item(1L, "item", "description", true, owner, null);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, Statuses.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        assertThatThrownBy(() -> bookingService.findBooking(bookingId, userId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Просмотр возможен либо автором бронирования, либо владельцем вещи");
    }

    @Test
    void findAllBookingsByUserWhenStateAllShouldReturnAllBookings() {
        Long userId = 1L;
        String state = "ALL";
        User user = new User(userId, "user", "user@email.com");
        Item item = new Item(1L, "item", "description", true, new User(2L, "owner", "owner@email.com"), null);
        List<Booking> bookings = List.of(
                new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, user, Statuses.APPROVED),
                new Booking(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, user, Statuses.WAITING)
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerId(userId)).thenReturn(bookings);

        List<BookingDto> result = bookingService.findAllBookingsByUser(userId, state);

        assertThat(result).hasSize(2);
        verify(bookingRepository, times(1)).findAllByBookerId(userId);
    }

    @Test
    void findAllBookingsByUserWhenStateCurrentShouldReturnCurrentBookings() {
        Long userId = 1L;
        String state = "CURRENT";
        User user = new User(userId, "user", "user@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllCurrentBookingByBookerId(userId)).thenReturn(List.of());

        List<BookingDto> result = bookingService.findAllBookingsByUser(userId, state);

        assertThat(result).isEmpty();
        verify(bookingRepository, times(1)).findAllCurrentBookingByBookerId(userId);
    }

    @Test
    void findAllBookingsByUserWhenStateWaitingShouldReturnWaitingBookings() {
        Long userId = 1L;
        String state = "WAITING";
        User user = new User(userId, "user", "user@email.com");
        Item item = new Item(1L, "item", "description", true, new User(2L, "owner", "owner@email.com"), null);
        List<Booking> bookings = List.of(
                new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, user, Statuses.WAITING)
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatus(userId, Statuses.WAITING)).thenReturn(bookings);

        List<BookingDto> result = bookingService.findAllBookingsByUser(userId, state);

        assertThat(result).hasSize(1);
        verify(bookingRepository, times(1)).findAllByBookerIdAndStatus(userId, Statuses.WAITING);
    }

    @Test
    void findAllBookingsByOwnerItemsWhenStateAllShouldReturnAllBookings() {
        Long userId = 1L;
        String state = "ALL";
        User user = new User(userId, "user", "user@email.com");
        Item item = new Item(1L, "item", "description", true, user, null);
        List<Booking> bookings = List.of(
                new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item,
                        new User(2L, "booker", "booker@email.com"), Statuses.APPROVED)
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByOwnerId(userId)).thenReturn(bookings);

        List<BookingDto> result = bookingService.findAllBookingsByOwnerItems(userId, state);

        assertThat(result).hasSize(1);
        verify(bookingRepository, times(1)).findAllByOwnerId(userId);
    }

    @Test
    void findAllBookingsByOwnerItemsWhenStateRejectedShouldReturnRejectedBookings() {
        Long userId = 1L;
        String state = "REJECTED";
        User user = new User(userId, "user", "user@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByOwnerIdAndStatus(userId, Statuses.REJECTED)).thenReturn(List.of());

        List<BookingDto> result = bookingService.findAllBookingsByOwnerItems(userId, state);

        assertThat(result).isEmpty();
        verify(bookingRepository, times(1)).findAllByOwnerIdAndStatus(userId, Statuses.REJECTED);
    }

    @Test
    void updateBookingWhenValidDataShouldUpdateBooking() {
        Long userId = 1L;
        Long bookingId = 1L;
        User user = new User(userId, "user", "user@email.com");
        Item item = new Item(1L, "item", "description", true, user, null);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, Statuses.WAITING);
        UpdateBookingRequest request = new UpdateBookingRequest(bookingId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.updateBooking(userId, request);

        assertThat(result).isNotNull();
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void updateBookingWhenIdIsNullShouldThrowException() {
        Long userId = 1L;
        UpdateBookingRequest request = new UpdateBookingRequest(null, null, null, null, null, null);


        assertThatThrownBy(() -> bookingService.updateBooking(userId, request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("id бронирования должен быть указан");
    }

    @Test
    void deleteBookingWhenValidDataShouldDeleteBooking() {
        Long bookingId = 1L;
        User user = new User(1L, "user", "user@email.com");
        Item item = new Item(1L, "item", "description", true, new User(2L, "owner", "owner@email.com"), null);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, Statuses.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        doNothing().when(bookingRepository).delete(booking);

        bookingService.deleteBooking(bookingId);

        verify(bookingRepository, times(1)).delete(booking);
    }

    @Test
    void approveBookingWhenValidApprovalShouldApproveBooking() {
        Long bookingId = 1L;
        Long userId = 1L;
        Boolean approved = true;
        User owner = new User(userId, "owner", "owner@email.com");
        User booker = new User(2L, "booker", "booker@email.com");
        Item item = new Item(1L, "item", "description", true, owner, null);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, Statuses.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        BookingDto result = bookingService.approveBooking(bookingId, userId, approved);

        assertThat(result).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(Statuses.APPROVED);
    }

    @Test
    void approveBookingWhenValidRejectionShouldRejectBooking() {
        Long bookingId = 1L;
        Long userId = 1L;
        Boolean approved = false;
        User owner = new User(userId, "owner", "owner@email.com");
        User booker = new User(2L, "booker", "booker@email.com");
        Item item = new Item(1L, "item", "description", true, owner, null);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, Statuses.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        BookingDto result = bookingService.approveBooking(bookingId, userId, approved);

        assertThat(result).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(Statuses.REJECTED);
    }

    @Test
    void approveBookingWhenUserNotOwnerShouldThrowException() {
        Long bookingId = 1L;
        Long userId = 2L;
        Boolean approved = true;
        User owner = new User(1L, "owner", "owner@email.com");
        User booker = new User(2L, "booker", "booker@email.com");
        Item item = new Item(1L, "item", "description", true, owner, null);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, Statuses.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));


        assertThatThrownBy(() -> bookingService.approveBooking(bookingId, userId, approved))
                .isInstanceOf(NotItemOwnerException.class)
                .hasMessageContaining("Менять статус вещи может только её владелец");
    }

    @Test
    void approveBookingWhenStatusNotWaitingShouldThrowException() {
        Long bookingId = 1L;
        Long userId = 1L;
        Boolean approved = true;
        User owner = new User(userId, "owner", "owner@email.com");
        User booker = new User(2L, "booker", "booker@email.com");
        Item item = new Item(1L, "item", "description", true, owner, null);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, Statuses.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));


        assertThatThrownBy(() -> bookingService.approveBooking(bookingId, userId, approved))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Вещь уже забронирована");
    }
}