package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class NewBookingRequestJsonTest {

    @Autowired
    private JacksonTester<NewBookingRequest> newBookingRequestJson;

    @Test
    public void testNewBookingRequestSerialization() throws IOException {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 2, 10, 0);
        NewBookingRequest request = new NewBookingRequest(start, end, 1L, 2L);

        JsonContent<NewBookingRequest> content = newBookingRequestJson.write(request);

        assertThat(content).extractingJsonPathStringValue("$.start").isEqualTo("2024-01-01T10:00:00");
        assertThat(content).extractingJsonPathStringValue("$.end").isEqualTo("2024-01-02T10:00:00");
        assertThat(content).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(content).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
    }

    @Test
    public void testNewBookingRequestDeserialization() throws IOException {
        String json = "{" +
                "\"start\": \"2024-01-01T10:00:00\"," +
                "\"end\": \"2024-01-02T10:00:00\"," +
                "\"itemId\": 1," +
                "\"bookerId\": 2" +
                "}";

        NewBookingRequest request = newBookingRequestJson.parseObject(json);

        assertThat(request).isNotNull();
        assertThat(request.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(request.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
        assertThat(request.getItemId()).isEqualTo(1L);
        assertThat(request.getBookerId()).isEqualTo(2L);
    }

    @Test
    public void testNewBookingRequestDeserializationWithNullValues() throws IOException {
        String json = "{" +
                "\"start\": \"2024-01-01T10:00:00\"," +
                "\"end\": \"2024-01-02T10:00:00\"" +
                "}";

        NewBookingRequest request = newBookingRequestJson.parseObject(json);

        assertThat(request).isNotNull();
        assertThat(request.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(request.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
        assertThat(request.getItemId()).isNull();
        assertThat(request.getBookerId()).isNull();
    }
}