package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
public class UpdateItemRequest {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @Positive
    private Long owner;
    @Positive
    private Long request;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasAvailable() {
        return available != null;
    }
}
