package com.example.weighttrackercalendar.modules.auth.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "La contraseña actual es obligatoria")
        String currentPassword,
        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 8, max = 120, message = "La nueva contraseña debe tener entre 8 y 120 caracteres")
        String newPassword
) {
}
