package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController("serverBookingController")
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    private static final String PATH = "/{id}";
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private static final String OWNER_PATH = "/owner";
    private static final String ID = "id";

    @GetMapping(PATH)
    public BookingDto findBooking(@PathVariable(ID) Long bookingId,
                                  @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return bookingService.findBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllBookingsByUser(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByUser(userId, state);
    }

    @GetMapping(OWNER_PATH)
    public List<BookingDto> findAllBookingsByOwnerItems(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                        @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByOwnerItems(userId, state);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestHeader(X_SHARER_USER_ID) Long userId,
                             @RequestBody NewBookingRequest booking) {
        return bookingService.createBooking(userId, booking);
    }

    @PutMapping
    public BookingDto update(@RequestHeader(X_SHARER_USER_ID) Long userId,
                             @RequestBody UpdateBookingRequest request) {
        return bookingService.updateBooking(userId, request);
    }

    @DeleteMapping(PATH)
    public void delete(@PathVariable(ID) Long bookingId) {
        bookingService.deleteBooking(bookingId);
    }

    @PatchMapping(PATH)
    public BookingDto approveBooking(@PathVariable(ID) Long bookingId,
                                     @RequestHeader(X_SHARER_USER_ID) Long userId,
                                     @RequestParam(name = "approved", defaultValue = "false") Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);

    }

}
