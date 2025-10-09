package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.enums.States;

@Controller("gatewayBookingController")
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    public final String path = "/{id}";
    public final String xSharerUserId = "X-Sharer-User-Id";
    public final String ownerPath = "/owner";
    public final String id = "id";

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @GetMapping(path)
    public ResponseEntity<Object> findBooking(@PathVariable(id) Long bookingId,
                                  @RequestHeader(xSharerUserId) Long userId) {
        return bookingClient.findBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingsByUser(@RequestHeader(xSharerUserId) Long userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingClient.findAllBookingsByUser(userId, state);
    }

    @GetMapping(ownerPath)
    public ResponseEntity<Object> findAllBookingsByOwnerItems(@RequestHeader(xSharerUserId) Long userId,
                                                        @RequestParam(name = "state", defaultValue = "ALL") String stateParameter) {
        States state = States.from(stateParameter)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParameter));
        return bookingClient.findAllBookingsByOwnerItems(userId, state);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(xSharerUserId) Long userId,
                             @RequestBody NewBookingRequest booking) {
        return bookingClient.createBooking(userId, booking);
    }

    @PutMapping
    public ResponseEntity<Object> update(@RequestHeader(xSharerUserId) Long userId,
                             @RequestBody UpdateBookingRequest request) {
        return bookingClient.updateBooking(userId, request);
    }

    @DeleteMapping(path)
    public ResponseEntity<Object> delete(@PathVariable(id) Long bookingId) {
        return bookingClient.deleteBooking(bookingId);
    }

    @PatchMapping(path)
    public ResponseEntity<Object> approveBooking(@PathVariable(id) Long bookingId,
                                                 @RequestHeader(xSharerUserId) Long userId,
                                                 @RequestParam(name = "approved", defaultValue = "false") Boolean approved) {
        return bookingClient.approveBooking(bookingId, userId, approved);

    }
}