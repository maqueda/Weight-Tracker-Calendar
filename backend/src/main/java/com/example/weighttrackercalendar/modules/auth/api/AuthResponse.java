package com.example.weighttrackercalendar.modules.auth.api;

public record AuthResponse(
        String token,
        AuthUserResponse user
) {
}
