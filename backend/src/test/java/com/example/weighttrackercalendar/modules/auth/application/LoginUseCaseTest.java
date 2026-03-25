package com.example.weighttrackercalendar.modules.auth.application;

import com.example.weighttrackercalendar.modules.auth.api.AuthResponse;
import com.example.weighttrackercalendar.modules.auth.api.LoginRequest;
import com.example.weighttrackercalendar.modules.auth.domain.AppUser;
import com.example.weighttrackercalendar.modules.auth.domain.AppUserRepository;
import com.example.weighttrackercalendar.modules.auth.domain.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoginUseCaseTest {

    private final AppUserRepository appUserRepository = mock(AppUserRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService = new JwtService(
            "WeightTrackerCalendarDevelopmentJwtSecretKey1234567890",
            60
    );
    private final LoginUseCase useCase = new LoginUseCase(
            appUserRepository,
            passwordEncoder,
            jwtService,
            new AuthMapper()
    );

    @Test
    void shouldLoginWhenCredentialsAreValid() {
        AppUser appUser = new AppUser(
                1L,
                "juanmaqueda",
                "Juan Maqueda Vargas",
                "juan@weight-tracker-calendar.local",
                "Europe/Madrid",
                passwordEncoder.encode("ChangeMe123!"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(appUserRepository.findByUsername("juanmaqueda")).thenReturn(Optional.of(appUser));

        AuthResponse response = useCase.handle(new LoginRequest("juanmaqueda", "ChangeMe123!"));

        assertThat(response.token()).isNotBlank();
        assertThat(response.user().username()).isEqualTo("juanmaqueda");
    }

    @Test
    void shouldRejectInvalidPassword() {
        AppUser appUser = new AppUser(
                1L,
                "juanmaqueda",
                "Juan Maqueda Vargas",
                "juan@weight-tracker-calendar.local",
                "Europe/Madrid",
                passwordEncoder.encode("ChangeMe123!"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(appUserRepository.findByUsername("juanmaqueda")).thenReturn(Optional.of(appUser));

        assertThatThrownBy(() -> useCase.handle(new LoginRequest("juanmaqueda", "OtraPassword123!")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
