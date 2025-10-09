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

    public final String path = "/{id}";
    public final String xSharerPath = "X-Sharer-User-Id";
    public final String ownerPath = "/owner";
    public final String id = "id";

    @GetMapping(path)
    public BookingDto findBooking(@PathVariable(id) Long bookingId,
                                  @RequestHeader(xSharerPath) Long userId) {
        return bookingService.findBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllBookingsByUser(@RequestHeader(xSharerPath) Long userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByUser(userId, state);
    }

    @GetMapping(ownerPath)
    public List<BookingDto> findAllBookingsByOwnerItems(@RequestHeader(xSharerPath) Long userId,
                                                        @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByOwnerItems(userId, state);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestHeader(xSharerPath) Long userId,
                             @RequestBody NewBookingRequest booking) {
        return bookingService.createBooking(userId, booking);
    }

    @PutMapping
    public BookingDto update(@RequestHeader(xSharerPath) Long userId,
                             @RequestBody UpdateBookingRequest request) {
        return bookingService.updateBooking(userId, request);
    }

    @DeleteMapping(path)
    public void delete(@PathVariable(id) Long bookingId) {
        bookingService.deleteBooking(bookingId);
    }

    @PatchMapping(path)
    public BookingDto approveBooking(@PathVariable(id) Long bookingId,
                                     @RequestHeader(xSharerPath) Long userId,
                                     @RequestParam(name = "approved", defaultValue = "false") Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);

    }

}
