package com.example.weighttrackercalendar.modules.auth.application;

import com.example.weighttrackercalendar.modules.auth.domain.AppUser;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(
            "WeightTrackerCalendarDevelopmentJwtSecretKey1234567890",
            60
    );

    @Test
    void shouldGenerateAndParseAuthenticatedUser() {
        AppUser user = new AppUser(
                1L,
                "juanmaqueda",
                "Juan Maqueda Vargas",
                "juan@weight-tracker-calendar.local",
                "Europe/Madrid",
                "hash",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        String token = jwtService.generateToken(user);
        AuthenticatedUser authenticatedUser = jwtService.parseAuthenticatedUser(token);

        assertThat(authenticatedUser.id()).isEqualTo(1L);
        assertThat(authenticatedUser.username()).isEqualTo("juanmaqueda");
        assertThat(authenticatedUser.displayName()).isEqualTo("Juan Maqueda Vargas");
    }
}
