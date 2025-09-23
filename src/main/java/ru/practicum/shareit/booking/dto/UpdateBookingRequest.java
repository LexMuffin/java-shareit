package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.enums.Statuses;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UpdateBookingRequest {

    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @Positive
    private Long itemId;
    @Positive
    private Long bookerId;
    private Statuses status;

    public boolean hasStart() {
        return this.startDate != null;
    }

    public boolean hasEnd() {
        return this.endDate != null;
    }
}
