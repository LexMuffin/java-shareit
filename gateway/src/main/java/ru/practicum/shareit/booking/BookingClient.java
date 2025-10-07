package ru.practicum.shareit.booking;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.enums.States;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build()
        );
    }

    public ResponseEntity<Object> createBooking(Long userId, NewBookingRequest request) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> findBooking(Long bookingId, Long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findAllBookingsByUser(Long userId, String state) {
        Map<String, Object> parameters = Map.of(
                "state", state
        );
        return get("?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> findAllBookingsByOwnerItems(Long userId, States state) {
        Map<String, Object> parameters = Map.of(
                "state", state
        );
        return get("?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> updateBooking(Long userId, UpdateBookingRequest request) {
        return put("", userId, request);
    }

    public ResponseEntity<Object> deleteBooking(Long bookingId) {
        return delete("" + bookingId);
    }

    public ResponseEntity<Object> approveBooking(Long bookingId, Long userId, Boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }
}