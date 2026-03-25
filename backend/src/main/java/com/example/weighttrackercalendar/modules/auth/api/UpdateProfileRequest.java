package com.example.weighttrackercalendar.modules.auth.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "El usuario es obligatorio")
        @Size(min = 3, max = 50, message = "El usuario debe tener entre 3 y 50 caracteres")
        String username,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
        String firstName,
        @Size(max = 120, message = "Los apellidos no pueden superar 120 caracteres")
        String lastName,
        @Size(max = 150, message = "El email no puede superar 150 caracteres")
        String email
) {
}
