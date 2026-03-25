package com.example.weighttrackercalendar.modules.auth.api;

public record AuthUserResponse(
        Long id,
        String username,
        String displayName,
        String firstName,
        String lastName,
        String email,
        String timezone
) {
}
