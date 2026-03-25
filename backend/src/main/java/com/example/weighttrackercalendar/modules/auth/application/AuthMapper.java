package com.example.weighttrackercalendar.modules.auth.application;

import com.example.weighttrackercalendar.modules.auth.api.AuthResponse;
import com.example.weighttrackercalendar.modules.auth.api.AuthUserResponse;
import com.example.weighttrackercalendar.modules.auth.domain.AppUser;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthUserResponse toUserResponse(AppUser appUser) {
        String[] nameParts = splitDisplayName(appUser.displayName());
        return new AuthUserResponse(
                appUser.id(),
                appUser.username(),
                appUser.displayName(),
                nameParts[0],
                nameParts[1],
                appUser.email(),
                appUser.timezone()
        );
    }

    public AuthResponse toAuthResponse(String token, AppUser appUser) {
        return new AuthResponse(token, toUserResponse(appUser));
    }

    private String[] splitDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            return new String[]{"", ""};
        }

        String normalized = displayName.trim().replaceAll("\\s+", " ");
        int separator = normalized.indexOf(' ');

        if (separator < 0) {
            return new String[]{normalized, ""};
        }

        return new String[]{
                normalized.substring(0, separator),
                normalized.substring(separator + 1)
        };
    }
}
