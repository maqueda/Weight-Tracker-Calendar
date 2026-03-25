package com.example.weighttrackercalendar.modules.auth.application;

import com.example.weighttrackercalendar.modules.auth.api.ChangePasswordRequest;
import com.example.weighttrackercalendar.modules.auth.api.SimpleMessageResponse;
import com.example.weighttrackercalendar.modules.auth.domain.AppUser;
import com.example.weighttrackercalendar.modules.auth.domain.AppUserRepository;
import com.example.weighttrackercalendar.modules.auth.domain.CurrentPasswordMismatchException;
import com.example.weighttrackercalendar.shared.application.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChangePasswordUseCaseTest {

    private final CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
    private final AppUserRepository appUserRepository = mock(AppUserRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ChangePasswordUseCase useCase = new ChangePasswordUseCase(
            currentUserProvider,
            appUserRepository,
            passwordEncoder
    );

    @Test
    void shouldChangePasswordWhenCurrentPasswordIsValid() {
        AppUser appUser = userWithPassword("ChangeMe123!");
        when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(appUser));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SimpleMessageResponse response = useCase.handle(new ChangePasswordRequest("ChangeMe123!", "NuevaClave123!"));

        assertThat(response.message()).isEqualTo("Contraseña actualizada correctamente.");
    }

    @Test
    void shouldRejectInvalidCurrentPassword() {
        AppUser appUser = userWithPassword("ChangeMe123!");
        when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(appUser));

        assertThatThrownBy(() -> useCase.handle(new ChangePasswordRequest("incorrecta", "NuevaClave123!")))
                .isInstanceOf(CurrentPasswordMismatchException.class);
    }

    private AppUser userWithPassword(String rawPassword) {
        return new AppUser(
                1L,
                "juanmaqueda",
                "Juan Maqueda Vargas",
                "juan@weight-tracker-calendar.local",
                "Europe/Madrid",
                passwordEncoder.encode(rawPassword),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
