package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Statuses;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId);

    @Query("select b from Booking as b where b.item.owner.id = ?1")
    List<Booking> findAllByOwnerId(Long ownerId);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, Statuses status);

    @Query("select b from Booking b where b.booker.id = ?1 and current_timestamp between b.start and b.end")
    List<Booking> findAllCurrentBookingByBookerId(Long bookerId);

    @Query("select b from Booking b where b.booker.id = ?1 and current_timestamp > b.end")
    List<Booking> findAllPastBookingByBookerId(Long bookerId);

    @Query("select b from Booking b where b.booker.id = ?1 and current_timestamp < b.start")
    List<Booking> findAllFutureBookingByBookerId(Long bookerId);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2")
    List<Booking> findAllByOwnerIdAndStatus(Long bookerId, Statuses status);

    @Query("select b from Booking b where b.item.owner.id = ?1 and current_timestamp between b.start and b.end")
    List<Booking> findAllCurrentBookingByOwnerId(Long bookerId);

    @Query("select b from Booking b where b.item.owner.id = ?1 and current_timestamp > b.end")
    List<Booking> findAllPastBookingByOwnerId(Long bookerId);

    @Query("select b from Booking b where b.item.owner.id = ?1 and current_timestamp < b.start")
    List<Booking> findAllFutureBookingByOwnerId(Long bookerId);

    @Query("select b.end from Booking b where b.item.id = ?1 and b.status = ?2 and b.end < ?3 order by b.end desc")
    List<LocalDateTime> findPastBookingEndByItemId(Long itemId, Statuses status, LocalDateTime datetime);

    @Query("select b.start from Booking b where b.item.id = ?1 and b.status = ?2 " +
            "and b.start >= ?3 order by b.start")
    List<LocalDateTime> findFutureBookingEndByItemId(Long itemId, Statuses status, LocalDateTime datetime);

    @Query("select b from Booking b where b.item.id in (?1) and b.status = ?2 " +
            "and b.end < ?3 order by b.end desc")
    List<Booking> findPastBookingEndDatesForItems(List<Long> itemIds, Statuses status, LocalDateTime datetime);

    @Query("select b from Booking b where b.item.id in (?1) and b.status = ?2 " +
            "and b.start >= ?3 order by b.start")
    List<Booking> findFutureBookingStartDatesForItems(List<Long> itemIds, Statuses status, LocalDateTime datetime);

    Boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime datetime);
}
