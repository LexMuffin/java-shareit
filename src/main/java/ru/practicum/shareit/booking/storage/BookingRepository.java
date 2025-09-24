package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Statuses;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId);

    @Query("select b from Booking as b where b.item.owner.id = :ownerId")
    List<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId);

    List<Booking> findAllByBookerIdAndStatus(@Param("bookerId") Long bookerId, @Param("status") Statuses status);

    @Query("select b from Booking b where b.booker.id = :bookerId and current_timestamp between b.start and b.end")
    List<Booking> findAllCurrentBookingByBookerId(@Param("bookerId") Long bookerId);

    @Query("select b from Booking b where b.booker.id = :bookerId and current_timestamp > b.end")
    List<Booking> findAllPastBookingByBookerId(@Param("bookerId") Long bookerId);

    @Query("select b from Booking b where b.booker.id = :bookerId and current_timestamp < b.start")
    List<Booking> findAllFutureBookingByBookerId(@Param("bookerId") Long bookerId);

    @Query("select b from Booking b where b.item.owner.id = :bookerId and b.status = :status")
    List<Booking> findAllByOwnerIdAndStatus(@Param("bookerId") Long bookerId, @Param("status")  Statuses status);

    @Query("select b from Booking b where b.item.owner.id = :bookerId and current_timestamp between b.start and b.end")
    List<Booking> findAllCurrentBookingByOwnerId(@Param("bookerId") Long bookerId);

    @Query("select b from Booking b where b.item.owner.id = :bookerId and current_timestamp > b.end")
    List<Booking> findAllPastBookingByOwnerId(@Param("bookerId") Long bookerId);

    @Query("select b from Booking b where b.item.owner.id = :bookerId and current_timestamp < b.start")
    List<Booking> findAllFutureBookingByOwnerId(@Param("bookerId") Long bookerId);

    @Query("select b.end from Booking b where b.item.id = :itemId and b.status = :status and b.end < :datetime order by b.end desc")
    List<LocalDateTime> findPastBookingEndByItemId(@Param("itemId") Long itemId, @Param("status") Statuses status,
                                                   @Param("datetime") LocalDateTime datetime);

    @Query("select b.start from Booking b where b.item.id = :itemId and b.status = :status " +
            "and b.start >= :datetime order by b.start")
    List<LocalDateTime> findFutureBookingEndByItemId(@Param("itemId") Long itemId, @Param("status") Statuses status,
                                                     @Param("datetime") LocalDateTime datetime);

    @Query("select b from Booking b where b.item.id in (:itemIds) and b.status = :status " +
            "and b.end < :datetime order by b.end desc")
    List<Booking> findPastBookingEndDatesForItems(@Param("itemIds") List<Long> itemIds, @Param("status") Statuses status,
                                                  @Param("datetime") LocalDateTime datetime);

    @Query("select b from Booking b where b.item.id in (:itemIds) and b.status = :status " +
            "and b.start >= :datetime order by b.start")
    List<Booking> findFutureBookingStartDatesForItems(@Param("itemIds") List<Long> itemIds, @Param("status") Statuses status,
                                                      @Param("datetime") LocalDateTime datetime);

    Boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime datetime);
}
