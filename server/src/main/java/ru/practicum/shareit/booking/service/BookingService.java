package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, NewBookingRequest request);

    BookingDto findBooking(Long bookingId, Long userId);

    List<BookingDto> findAllBookingsByUser(Long userId, String state);

    List<BookingDto> findAllBookingsByOwnerItems(Long userId, String state);

    BookingDto updateBooking(Long userId, UpdateBookingRequest request);

    void deleteBooking(Long bookingId);

    BookingDto approveBooking(Long bookingId, Long userId, Boolean approved);
}
