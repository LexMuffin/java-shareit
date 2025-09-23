package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.controller.HeaderConstants;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final String path = "/{booking-id}";
    private final String owner = "/owner";
    private final String bPath = "booking-id";

    @GetMapping(path)
    public BookingDto findBooking(@PathVariable(bPath) Long bookingId,
                                  @RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId) {
        return bookingService.findBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllBookingsByUser(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByUser(userId, state);
    }

    @GetMapping(owner)
    public List<BookingDto> findAllBookingsByOwnerItems(@Valid @PathVariable("id") Long itemId,
                                                        @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByOwnerItems(itemId, state);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                             @RequestBody NewBookingRequest booking) {
        return bookingService.createBooking(userId, booking);
    }

    @PutMapping
    public BookingDto update(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                             @RequestBody UpdateBookingRequest request) {
        return bookingService.updateBooking(userId, request);
    }

    @DeleteMapping(path)
    public void delete(@PathVariable(bPath) Long bookingId) {
        bookingService.deleteBooking(bookingId);
    }

    @PatchMapping(path)
    public BookingDto approveBooking(@PathVariable(bPath) Long bookingId,
                                     @RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                     @RequestParam(name = "approved", defaultValue = "false") Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);

    }

}
