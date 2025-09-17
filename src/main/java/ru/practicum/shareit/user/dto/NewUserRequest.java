package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"email"})
public class NewUserRequest {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
