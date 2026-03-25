package com.example.weighttrackercalendar.modules.auth.application;

import com.example.weighttrackercalendar.modules.auth.api.AuthResponse;
import com.example.weighttrackercalendar.modules.auth.api.RegisterUserRequest;
import com.example.weighttrackercalendar.modules.auth.domain.AppUser;
import com.example.weighttrackercalendar.modules.auth.domain.AppUserRepository;
import com.example.weighttrackercalendar.modules.auth.domain.UsernameAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegisterUserUseCaseTest {

    private final AppUserRepository appUserRepository = mock(AppUserRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService = new JwtService(
            "WeightTrackerCalendarDevelopmentJwtSecretKey1234567890",
            60
    );
    private final RegisterUserUseCase useCase = new RegisterUserUseCase(
            appUserRepository,
            passwordEncoder,
            jwtService,
            new AuthMapper()
    );

    @Test
    void shouldRegisterNewUserAndReturnToken() {
        when(appUserRepository.existsByUsername("nuevo")).thenReturn(false);
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            return new AppUser(
                    2L,
                    user.username(),
                    user.displayName(),
                    user.email(),
                    user.timezone(),
                    user.passwordHash(),
                    true,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
        });

        AuthResponse response = useCase.handle(new RegisterUserRequest(
                "nuevo",
                "Nuevo Usuario",
                "nuevo@example.com",
                "Secret123!"
        ));

        assertThat(response.token()).isNotBlank();
        assertThat(response.user().id()).isEqualTo(2L);
        assertThat(response.user().username()).isEqualTo("nuevo");
    }

    @Test
    void shouldRejectDuplicatedUsername() {
        when(appUserRepository.existsByUsername("juanmaqueda")).thenReturn(true);

        assertThatThrownBy(() -> useCase.handle(new RegisterUserRequest(
                "juanmaqueda",
                "Juan",
                null,
                "Secret123!"
        ))).isInstanceOf(UsernameAlreadyExistsException.class);
    }
}
