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

    @GetMapping(BookingControllerHeaderConstants.PATH)
    public BookingDto findBooking(@PathVariable(BookingControllerHeaderConstants.ID) Long bookingId,
                                  @RequestHeader(BookingControllerHeaderConstants.X_SHARER_USER_ID) Long userId) {
        return bookingService.findBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllBookingsByUser(@RequestHeader(BookingControllerHeaderConstants.X_SHARER_USER_ID) Long userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByUser(userId, state);
    }

    @GetMapping(BookingControllerHeaderConstants.OWNER_PATH)
    public List<BookingDto> findAllBookingsByOwnerItems(@RequestHeader(BookingControllerHeaderConstants.X_SHARER_USER_ID) Long userId,
                                                        @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByOwnerItems(userId, state);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestHeader(BookingControllerHeaderConstants.X_SHARER_USER_ID) Long userId,
                             @RequestBody NewBookingRequest booking) {
        return bookingService.createBooking(userId, booking);
    }

    @PutMapping
    public BookingDto update(@RequestHeader(BookingControllerHeaderConstants.X_SHARER_USER_ID) Long userId,
                             @RequestBody UpdateBookingRequest request) {
        return bookingService.updateBooking(userId, request);
    }

    @DeleteMapping(BookingControllerHeaderConstants.PATH)
    public void delete(@PathVariable(BookingControllerHeaderConstants.ID) Long bookingId) {
        bookingService.deleteBooking(bookingId);
    }

    @PatchMapping(BookingControllerHeaderConstants.PATH)
    public BookingDto approveBooking(@PathVariable(BookingControllerHeaderConstants.ID) Long bookingId,
                                     @RequestHeader(BookingControllerHeaderConstants.X_SHARER_USER_ID) Long userId,
                                     @RequestParam(name = "approved", defaultValue = "false") Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);

    }

}
