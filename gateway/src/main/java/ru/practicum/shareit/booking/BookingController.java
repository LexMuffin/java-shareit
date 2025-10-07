package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.BookingControllerHeaderConstants;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.enums.States;

@Controller("gatewayBookingController")
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @GetMapping(BookingControllerHeaderConstants.PATH)
    public ResponseEntity<Object> findBooking(@PathVariable(BookingControllerHeaderConstants.ID) Long bookingId,
                                  @RequestHeader(BookingControllerHeaderConstants.X_SHARER_USER_ID) Long userId) {
        return bookingClient.findBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingsByUser(@RequestHeader(BookingControllerHeaderConstants.X_SHARER_USER_ID) Long userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingClient.findAllBookingsByUser(userId, state);
    }

    @GetMapping(BookingControllerHeaderConstants.OWNER_PATH)
    public ResponseEntity<Object> findAllBookingsByOwnerItems(@RequestHeader(BookingControllerHeaderConstants.X_SHARER_USER_ID) Long userId,
                                                        @RequestParam(name = "state", defaultValue = "ALL") String stateParameter) {
        States state = States.from(stateParameter)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParameter));
        return bookingClient.findAllBookingsByOwnerItems(userId, state);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(BookingControllerHeaderConstants.X_SHARER_USER_ID) Long userId,
                             @RequestBody NewBookingRequest booking) {
        return bookingClient.createBooking(userId, booking);
    }

    @PutMapping
    public ResponseEntity<Object> update(@RequestHeader(BookingControllerHeaderConstants.X_SHARER_USER_ID) Long userId,
                             @RequestBody UpdateBookingRequest request) {
        return bookingClient.updateBooking(userId, request);
    }

    @DeleteMapping(BookingControllerHeaderConstants.PATH)
    public ResponseEntity<Object> delete(@PathVariable(BookingControllerHeaderConstants.ID) Long bookingId) {
        return bookingClient.deleteBooking(bookingId);
    }

    @PatchMapping(BookingControllerHeaderConstants.PATH)
    public ResponseEntity<Object> approveBooking(@PathVariable(BookingControllerHeaderConstants.ID) Long bookingId,
                                                 @RequestHeader(BookingControllerHeaderConstants.X_SHARER_USER_ID) Long userId,
                                                 @RequestParam(name = "approved", defaultValue = "false") Boolean approved) {
        return bookingClient.approveBooking(bookingId, userId, approved);

    }
}