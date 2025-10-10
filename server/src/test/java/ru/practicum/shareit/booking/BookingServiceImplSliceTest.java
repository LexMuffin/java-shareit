package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.enums.Statuses;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(BookingServiceImpl.class)
class BookingServiceImplSliceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingServiceImpl bookingService;

    @Test
    void findAllByBookerIdShouldReturnUserBookings() {
        User user = createUser("booker", "booker@email.com");
        User owner = createUser("owner", "owner@email.com");
        Item item = createItem("item", "description", true, owner);
        Booking booking1 = createBooking(item, user, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), Statuses.APPROVED);
        Booking booking2 = createBooking(item, user, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Statuses.WAITING);

        List<Booking> result = bookingRepository.findAllByBookerId(user.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Booking::getBooker).containsOnly(user);
    }

    @Test
    void findAllByOwnerIdShouldReturnOwnerBookings() {
        User owner = createUser("owner", "owner@email.com");
        User booker1 = createUser("booker1", "booker1@email.com");
        User booker2 = createUser("booker2", "booker2@email.com");
        Item item = createItem("item", "description", true, owner);
        Booking booking1 = createBooking(item, booker1, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), Statuses.APPROVED);
        Booking booking2 = createBooking(item, booker2, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Statuses.WAITING);

        List<Booking> result = bookingRepository.findAllByOwnerId(owner.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Booking::getItem).extracting(Item::getOwner).containsOnly(owner);
    }

    @Test
    void findAllByBookerIdAndStatusShouldReturnFilteredBookings() {
        User user = createUser("booker", "booker@email.com");
        User owner = createUser("owner", "owner@email.com");
        Item item = createItem("item", "description", true, owner);
        Booking approved = createBooking(item, user, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), Statuses.APPROVED);
        Booking waiting = createBooking(item, user, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Statuses.WAITING);
        Booking rejected = createBooking(item, user, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), Statuses.REJECTED);

        List<Booking> waitingResult = bookingRepository.findAllByBookerIdAndStatus(user.getId(), Statuses.WAITING);
        List<Booking> approvedResult = bookingRepository.findAllByBookerIdAndStatus(user.getId(), Statuses.APPROVED);

        assertThat(waitingResult).hasSize(1).extracting(Booking::getStatus).containsOnly(Statuses.WAITING);
        assertThat(approvedResult).hasSize(1).extracting(Booking::getStatus).containsOnly(Statuses.APPROVED);
    }

    @Test
    void findAllPastBookingByBookerIdShouldReturnPastBookings() {
        User user = createUser("booker", "booker@email.com");
        User owner = createUser("owner", "owner@email.com");
        Item item = createItem("item", "description", true, owner);

        Booking past = createBooking(item, user, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), Statuses.APPROVED);
        createBooking(item, user, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(2), Statuses.APPROVED);
        createBooking(item, user, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Statuses.WAITING);

        List<Booking> result = bookingRepository.findAllPastBookingByBookerId(user.getId());

        assertThat(result.get(0).getId()).isEqualTo(past.getId());
    }

    @Test
    void findAllFutureBookingByBookerIdShouldReturnFutureBookings() {
        User user = createUser("booker", "booker@email.com");
        User owner = createUser("owner", "owner@email.com");
        Item item = createItem("item", "description", true, owner);

        Booking future = createBooking(item, user, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Statuses.WAITING);
        createBooking(item, user, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), Statuses.APPROVED);
        createBooking(item, user, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(2), Statuses.APPROVED);

        List<Booking> result = bookingRepository.findAllFutureBookingByBookerId(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(future.getId());
    }

    @Test
    void existsByBookerIdAndItemIdAndEndBeforeShouldReturnTrueWhenExists() {
        User user = createUser("booker", "booker@email.com");
        User owner = createUser("owner", "owner@email.com");
        Item item = createItem("item", "description", true, owner);

        createBooking(item, user, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), Statuses.APPROVED);
        createBooking(item, user, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Statuses.WAITING);

        Boolean exists = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(user.getId(), item.getId(), LocalDateTime.now());

        assertThat(exists).isTrue();
    }

    @Test
    void existsByBookerIdAndItemIdAndEndBeforeShouldReturnFalseWhenNotExists() {
        User user = createUser("booker", "booker@email.com");
        User owner = createUser("owner", "owner@email.com");
        Item item = createItem("item", "description", true, owner);

        createBooking(item, user, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Statuses.WAITING);

        Boolean exists = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(user.getId(), item.getId(), LocalDateTime.now());

        assertThat(exists).isFalse();
    }

    @Test
    void saveBookingShouldPersistCorrectly() {
        User user = createUser("booker", "booker@email.com");
        User owner = createUser("owner", "owner@email.com");
        Item item = createItem("item", "description", true, owner);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Statuses.WAITING);

        Booking saved = bookingRepository.save(booking);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getItem()).isEqualTo(item);
        assertThat(saved.getBooker()).isEqualTo(user);
        assertThat(saved.getStatus()).isEqualTo(Statuses.WAITING);
    }

    @Test
    void findByIdShouldReturnBookingWhenExists() {
        User user = createUser("booker", "booker@email.com");
        User owner = createUser("owner", "owner@email.com");
        Item item = createItem("item", "description", true, owner);
        Booking booking = createBooking(item, user, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Statuses.WAITING);

        Booking found = bookingRepository.findById(booking.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getItem()).isEqualTo(item);
        assertThat(found.getBooker()).isEqualTo(user);
        assertThat(found.getStatus()).isEqualTo(Statuses.WAITING);
    }

    @Test
    void deleteBookingShouldRemoveFromDatabase() {
        User user = createUser("booker", "booker@email.com");
        User owner = createUser("owner", "owner@email.com");
        Item item = createItem("item", "description", true, owner);
        Booking booking = createBooking(item, user, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Statuses.WAITING);

        bookingRepository.delete(booking);
        entityManager.flush();

        assertThat(bookingRepository.findById(booking.getId())).isEmpty();
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return entityManager.persistAndFlush(user);
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return entityManager.persistAndFlush(item);
    }

    private Booking createBooking(Item item, User booker, LocalDateTime start, LocalDateTime end, Statuses status) {
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return entityManager.persistAndFlush(booking);
    }
}