package com.example.weighttrackercalendar.modules.auth.domain;

import java.time.LocalDateTime;

public record AppUser(
        Long id,
        String username,
        String displayName,
        String email,
        String timezone,
        String passwordHash,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
