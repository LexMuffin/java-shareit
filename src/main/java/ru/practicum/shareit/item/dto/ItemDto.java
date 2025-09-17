package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    private String description;
    private Boolean available = Boolean.FALSE;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long owner;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long request;
}
