package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.Statuses;
import ru.practicum.shareit.item.service.ExtendedItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5433/share_it_test")
@ActiveProfiles("test")
public class BookingServiceImplIntegrationTest {

    private final EntityManager em;
    private final ExtendedItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @AfterEach
    public void cleanUp() {
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        em.createQuery("DELETE FROM Comment").executeUpdate();
        em.createQuery("DELETE FROM Booking").executeUpdate();
        em.createQuery("DELETE FROM Item").executeUpdate();
        em.createQuery("DELETE FROM ItemRequest").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();

        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();

        em.flush();
    }

    private void newUserCreation() {
        Query userQuery = em.createNativeQuery("INSERT INTO Users (id, name, email) " +
                "VALUES (:id, :name, :email);");
        userQuery.setParameter("id", 1L);
        userQuery.setParameter("name", "testName");
        userQuery.setParameter("email", "testEmail@email.com");
        userQuery.executeUpdate();
    }

    private void newUser2Creation() {
        Query userQuery = em.createNativeQuery("INSERT INTO Users (id, name, email) " +
                "VALUES (:id, :name, :email);");
        userQuery.setParameter("id", 2L);
        userQuery.setParameter("name", "testName2");
        userQuery.setParameter("email", "testEmail2@email.com");
        userQuery.executeUpdate();
    }


    private void newItemCreation() {
        Query itemQuery = em.createNativeQuery("INSERT INTO Items (id, name, description, is_available, owner_id, request_id) " +
                "VALUES (:id, :name, :description, :is_available, :owner_id, :request_id);");
        itemQuery.setParameter("id", 1L);
        itemQuery.setParameter("name", "testName");
        itemQuery.setParameter("description", "testDescription");
        itemQuery.setParameter("is_available", Boolean.TRUE);
        itemQuery.setParameter("owner_id", 1L);
        itemQuery.setParameter("request_id", null);
        itemQuery.executeUpdate();
    }

    private void newBookingCreation() {
        Query bookingQuery = em.createNativeQuery("INSERT INTO Bookings (id, start_date, end_date, item_id, booker_id, status) " +
                "VALUES (:id, :startDate, :endDate, :itemId, :bookerId, :status);");
        bookingQuery.setParameter("id", 1L);
        bookingQuery.setParameter("startDate", LocalDateTime.now().minusDays(2));
        bookingQuery.setParameter("endDate", LocalDateTime.now().minusDays(1));
        bookingQuery.setParameter("itemId", 1L);
        bookingQuery.setParameter("bookerId", 2L);
        bookingQuery.setParameter("status", "WAITING");
        bookingQuery.executeUpdate();
    }

    @Test
    public void testCreateBooking() {
        newUserCreation();
        newUser2Creation();
        newItemCreation();

        NewBookingRequest newBookingRequest = new NewBookingRequest(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                1L,
                2L);

        BookingDto booking = bookingService.createBooking(2L, newBookingRequest);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(newBookingRequest.getStart()));
        assertThat(booking.getEnd(), equalTo(newBookingRequest.getEnd()));


    }

    @Test
    public void testFindBooking() {
        newUserCreation();
        newUser2Creation();
        newItemCreation();
        newBookingCreation();

        BookingDto bookingByBooker = bookingService.findBooking(1L, 1L);

        assertThat(bookingByBooker.getId(), equalTo(1L));
        assertThat(bookingByBooker.getBooker().getId(), equalTo(2L));
        assertThat(bookingByBooker.getItem().getId(), equalTo(1L));
        assertThat(bookingByBooker.getStatus(), equalTo(Statuses.WAITING));
    }

    @Test
    public void testFindAllBookingsByUser() {
        newUserCreation();
        newUser2Creation();
        newItemCreation();
        newBookingCreation();

        Query itemQuery2 = em.createNativeQuery("INSERT INTO Items (id, name, description, is_available, owner_id) " +
                "VALUES (:id, :name, :description, :is_available, :owner_id)");
        itemQuery2.setParameter("id", 2L);
        itemQuery2.setParameter("name", "Дрель");
        itemQuery2.setParameter("description", "Мощная дрель");
        itemQuery2.setParameter("is_available", Boolean.TRUE);
        itemQuery2.setParameter("owner_id", 1L);
        itemQuery2.executeUpdate();

        Query bookingQuery = em.createNativeQuery("INSERT INTO Bookings (id, start_date, end_date, item_id, booker_id, status) " +
                "VALUES (:id, :startDate, :endDate, :itemId, :bookerId, :status);");
        bookingQuery.setParameter("id", 2L);
        bookingQuery.setParameter("startDate", LocalDateTime.now().minusDays(2));
        bookingQuery.setParameter("endDate", LocalDateTime.now().minusDays(1));
        bookingQuery.setParameter("itemId", 2L);
        bookingQuery.setParameter("bookerId", 2L);
        bookingQuery.setParameter("status", "WAITING");
        bookingQuery.executeUpdate();

        List<BookingDto> allBookings = bookingService.findAllBookingsByUser(2L, "ALL");

        assertThat(allBookings.get(0).getId(), equalTo(1L));
        assertThat(allBookings.get(0).getItem().getName(), equalTo("testName"));
        assertThat(allBookings.get(1).getId(), equalTo(2L));
        assertThat(allBookings.get(1).getItem().getName(), equalTo("Дрель"));


    }

    @Test
    public void testFindAllBookingsByOwnerItems() {
        newUserCreation();
        newUser2Creation();
        newItemCreation();

        Query itemQuery2 = em.createNativeQuery("INSERT INTO Items (id, name, description, is_available, owner_id) " +
                "VALUES (:id, :name, :description, :is_available, :owner_id)");
        itemQuery2.setParameter("id", 2L);
        itemQuery2.setParameter("name", "Дрель");
        itemQuery2.setParameter("description", "Мощная дрель");
        itemQuery2.setParameter("is_available", Boolean.TRUE);
        itemQuery2.setParameter("owner_id", 1L);
        itemQuery2.executeUpdate();

        newBookingCreation();

        Query bookingQuery = em.createNativeQuery("INSERT INTO Bookings (id, start_date, end_date, item_id, booker_id, status) " +
                "VALUES (:id, :startDate, :endDate, :itemId, :bookerId, :status);");
        bookingQuery.setParameter("id", 2L);
        bookingQuery.setParameter("startDate", LocalDateTime.now().minusDays(2));
        bookingQuery.setParameter("endDate", LocalDateTime.now().minusDays(1));
        bookingQuery.setParameter("itemId", 2L);
        bookingQuery.setParameter("bookerId", 2L);
        bookingQuery.setParameter("status", "WAITING");
        bookingQuery.executeUpdate();

        List<BookingDto> waitingBookings = bookingService.findAllBookingsByOwnerItems(1L, "WAITING");
        assertThat(waitingBookings.size(), equalTo(2));

        assertThat(waitingBookings.get(0).getItem().getOwner(), equalTo(1L));
        assertThat(waitingBookings.get(1).getItem().getOwner(), equalTo(1L));
        assertThat(waitingBookings.get(0).getItem().getName(), equalTo("testName"));
        assertThat(waitingBookings.get(1).getItem().getName(), equalTo("Дрель"));
    }

    @Test
    public void testUpdateBooking() {
        newUserCreation();
        newUser2Creation();
        newItemCreation();
        newBookingCreation();

        UpdateBookingRequest updateBookingRequest = new UpdateBookingRequest(
                1L,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                1L,
                2L,
                Statuses.APPROVED);

        BookingDto updatedBooking = bookingService.updateBooking(1L, updateBookingRequest);

        assertThat(updatedBooking.getId(), equalTo(1L));
        assertThat(updatedBooking.getStatus(), equalTo(Statuses.APPROVED));
        assertThat(updatedBooking.getItem().getId(), equalTo(1L));
    }

    @Test
    public void testDeleteBooking() {
        newUserCreation();
        newUser2Creation();
        newItemCreation();
        newBookingCreation();

        bookingService.deleteBooking(1L);

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.id = :id", Booking.class);
        query.setParameter("id", 1L);

        assertThrows(NoResultException.class, query::getSingleResult);
    }
}
