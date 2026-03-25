package com.example.weighttrackercalendar.modules.auth.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank(message = "El usuario es obligatorio")
        @Size(min = 3, max = 50, message = "El usuario debe tener entre 3 y 50 caracteres")
        String username,
        @NotBlank(message = "El nombre visible es obligatorio")
        @Size(min = 3, max = 150, message = "El nombre visible debe tener entre 3 y 150 caracteres")
        String displayName,
        @Size(max = 150, message = "El email no puede superar 150 caracteres")
        String email,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 120, message = "La contraseña debe tener entre 8 y 120 caracteres")
        String password
) {
}
