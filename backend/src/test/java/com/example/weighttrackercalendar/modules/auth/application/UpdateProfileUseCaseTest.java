package com.example.weighttrackercalendar.modules.auth.application;

import com.example.weighttrackercalendar.modules.auth.api.AuthUserResponse;
import com.example.weighttrackercalendar.modules.auth.api.UpdateProfileRequest;
import com.example.weighttrackercalendar.modules.auth.domain.AppUser;
import com.example.weighttrackercalendar.modules.auth.domain.AppUserRepository;
import com.example.weighttrackercalendar.modules.auth.domain.EmailAlreadyExistsException;
import com.example.weighttrackercalendar.modules.auth.domain.UsernameAlreadyExistsException;
import com.example.weighttrackercalendar.shared.application.CurrentUserProvider;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateProfileUseCaseTest {

    private final CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
    private final AppUserRepository appUserRepository = mock(AppUserRepository.class);
    private final UpdateProfileUseCase useCase = new UpdateProfileUseCase(
            currentUserProvider,
            appUserRepository,
            new AuthMapper()
    );

    @Test
    void shouldUpdateCurrentUserProfile() {
        AppUser currentUser = existingUser(1L, "juanmaqueda", "Juan Maqueda Vargas", "juan@weight-tracker-calendar.local");

        when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(appUserRepository.findByUsername("juanmaqueda")).thenReturn(Optional.of(currentUser));
        when(appUserRepository.findByEmail("juan.vargas@example.com")).thenReturn(Optional.empty());
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthUserResponse response = useCase.handle(new UpdateProfileRequest(
                "juanmaqueda",
                "Juan",
                "Maqueda Vargas",
                "Juan.Vargas@example.com"
        ));

        assertThat(response.username()).isEqualTo("juanmaqueda");
        assertThat(response.displayName()).isEqualTo("Juan Maqueda Vargas");
        assertThat(response.firstName()).isEqualTo("Juan");
        assertThat(response.lastName()).isEqualTo("Maqueda Vargas");
        assertThat(response.email()).isEqualTo("juan.vargas@example.com");
    }

    @Test
    void shouldRejectDuplicateUsernameFromAnotherUser() {
        AppUser currentUser = existingUser(1L, "juanmaqueda", "Juan Maqueda Vargas", "juan@weight-tracker-calendar.local");
        AppUser otherUser = existingUser(2L, "demo", "Usuario Demo", "demo@example.com");

        when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(appUserRepository.findByUsername("demo")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> useCase.handle(new UpdateProfileRequest(
                "demo",
                "Juan",
                "Maqueda",
                "juan@weight-tracker-calendar.local"
        ))).isInstanceOf(UsernameAlreadyExistsException.class);
    }

    @Test
    void shouldRejectDuplicateEmailFromAnotherUser() {
        AppUser currentUser = existingUser(1L, "juanmaqueda", "Juan Maqueda Vargas", "juan@weight-tracker-calendar.local");
        AppUser otherUser = existingUser(3L, "maria", "Maria Gomez", "maria@example.com");

        when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(appUserRepository.findByUsername("juanmaqueda")).thenReturn(Optional.of(currentUser));
        when(appUserRepository.findByEmail("maria@example.com")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> useCase.handle(new UpdateProfileRequest(
                "juanmaqueda",
                "Juan",
                "Maqueda",
                "maria@example.com"
        ))).isInstanceOf(EmailAlreadyExistsException.class);
    }

    private AppUser existingUser(Long id, String username, String displayName, String email) {
        LocalDateTime now = LocalDateTime.now();
        return new AppUser(
                id,
                username,
                displayName,
                email,
                "Europe/Madrid",
                "encoded-password",
                true,
                now,
                now
        );
    }
}
