package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"email"})
public class NewUserRequest {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
