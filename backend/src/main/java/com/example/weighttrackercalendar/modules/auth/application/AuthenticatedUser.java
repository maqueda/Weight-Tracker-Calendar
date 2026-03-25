package com.example.weighttrackercalendar.modules.auth.application;

public record AuthenticatedUser(
        Long id,
        String username,
        String displayName
) {
}
