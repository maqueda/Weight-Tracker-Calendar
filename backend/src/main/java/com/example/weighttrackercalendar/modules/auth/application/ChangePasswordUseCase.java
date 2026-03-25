package com.example.weighttrackercalendar.modules.auth.application;

import com.example.weighttrackercalendar.modules.auth.api.ChangePasswordRequest;
import com.example.weighttrackercalendar.modules.auth.api.SimpleMessageResponse;
import com.example.weighttrackercalendar.modules.auth.domain.AppUser;
import com.example.weighttrackercalendar.modules.auth.domain.AppUserRepository;
import com.example.weighttrackercalendar.modules.auth.domain.CurrentPasswordMismatchException;
import com.example.weighttrackercalendar.shared.application.CurrentUserProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChangePasswordUseCase {

    private final CurrentUserProvider currentUserProvider;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordUseCase(
            CurrentUserProvider currentUserProvider,
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.currentUserProvider = currentUserProvider;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public SimpleMessageResponse handle(ChangePasswordRequest request) {
        AppUser currentUser = appUserRepository.findById(currentUserProvider.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario autenticado."));

        if (!passwordEncoder.matches(request.currentPassword(), currentUser.passwordHash())) {
            throw new CurrentPasswordMismatchException();
        }

        if (request.currentPassword().equals(request.newPassword())) {
            throw new IllegalArgumentException("La nueva contraseña debe ser distinta de la actual.");
        }

        appUserRepository.save(new AppUser(
                currentUser.id(),
                currentUser.username(),
                currentUser.displayName(),
                currentUser.email(),
                currentUser.timezone(),
                passwordEncoder.encode(request.newPassword()),
                currentUser.active(),
                currentUser.createdAt(),
                LocalDateTime.now()
        ));

        return new SimpleMessageResponse("Contraseña actualizada correctamente.");
    }
}
