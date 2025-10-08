package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.enums.Statuses;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UpdateBookingRequestJsonTest {

    @Autowired
    private JacksonTester<UpdateBookingRequest> updateBookingRequestJson;

    @Test
    public void testUpdateBookingRequestSerialization() throws IOException {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 2, 10, 0);
        UpdateBookingRequest request = new UpdateBookingRequest(1L, start, end, 2L, 3L, Statuses.APPROVED);

        JsonContent<UpdateBookingRequest> content = updateBookingRequestJson.write(request);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.startDate").isEqualTo("2024-01-01T10:00:00");
        assertThat(content).extractingJsonPathStringValue("$.endDate").isEqualTo("2024-01-02T10:00:00");
        assertThat(content).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
        assertThat(content).extractingJsonPathNumberValue("$.bookerId").isEqualTo(3);
        assertThat(content).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    @Test
    public void testUpdateBookingRequestDeserialization() throws IOException {
        String json = "{" +
                "\"id\": 1," +
                "\"startDate\": \"2024-01-01T10:00:00\"," +
                "\"endDate\": \"2024-01-02T10:00:00\"," +
                "\"itemId\": 2," +
                "\"bookerId\": 3," +
                "\"status\": \"WAITING\"" +
                "}";

        UpdateBookingRequest request = updateBookingRequestJson.parseObject(json);

        assertThat(request).isNotNull();
        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getStartDate()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(request.getEndDate()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
        assertThat(request.getItemId()).isEqualTo(2L);
        assertThat(request.getBookerId()).isEqualTo(3L);
        assertThat(request.getStatus()).isEqualTo(Statuses.WAITING);
    }

    @Test
    public void testUpdateBookingRequestDeserializationWithPartialData() throws IOException {
        String json = "{" +
                "\"id\": 1," +
                "\"startDate\": \"2024-01-01T10:00:00\"," +
                "\"itemId\": 2" +
                "}";

        UpdateBookingRequest request = updateBookingRequestJson.parseObject(json);

        assertThat(request).isNotNull();
        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getStartDate()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(request.getEndDate()).isNull();
        assertThat(request.getItemId()).isEqualTo(2L);
        assertThat(request.getBookerId()).isNull();
        assertThat(request.getStatus()).isNull();
    }

    @Test
    public void testUpdateBookingRequestHelperMethods() throws IOException {
        String json = "{" +
                "\"id\": 1," +
                "\"startDate\": \"2024-01-01T10:00:00\"," +
                "\"endDate\": \"2024-01-02T10:00:00\"," +
                "\"itemId\": 2" +
                "}";

        UpdateBookingRequest request = updateBookingRequestJson.parseObject(json);

        assertThat(request).isNotNull();
        assertThat(request.hasStart()).isTrue();
        assertThat(request.hasEnd()).isTrue();

        UpdateBookingRequest requestWithoutDates = new UpdateBookingRequest(1L, null, null, 2L, 3L, Statuses.WAITING);
        assertThat(requestWithoutDates.hasStart()).isFalse();
        assertThat(requestWithoutDates.hasEnd()).isFalse();
    }
}