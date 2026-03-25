package com.example.weighttrackercalendar.modules.auth.application;

import com.example.weighttrackercalendar.modules.auth.api.AuthUserResponse;
import com.example.weighttrackercalendar.modules.auth.api.UpdateProfileRequest;
import com.example.weighttrackercalendar.modules.auth.domain.AppUser;
import com.example.weighttrackercalendar.modules.auth.domain.AppUserRepository;
import com.example.weighttrackercalendar.modules.auth.domain.EmailAlreadyExistsException;
import com.example.weighttrackercalendar.modules.auth.domain.UsernameAlreadyExistsException;
import com.example.weighttrackercalendar.shared.application.CurrentUserProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UpdateProfileUseCase {

    private final CurrentUserProvider currentUserProvider;
    private final AppUserRepository appUserRepository;
    private final AuthMapper authMapper;

    public UpdateProfileUseCase(
            CurrentUserProvider currentUserProvider,
            AppUserRepository appUserRepository,
            AuthMapper authMapper
    ) {
        this.currentUserProvider = currentUserProvider;
        this.appUserRepository = appUserRepository;
        this.authMapper = authMapper;
    }

    public AuthUserResponse handle(UpdateProfileRequest request) {
        AppUser currentUser = appUserRepository.findById(currentUserProvider.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("No se encontro el usuario autenticado."));

        String username = request.username().trim();
        String email = normalizeEmail(request.email());
        String displayName = buildDisplayName(request.firstName(), request.lastName());

        // Se valida unicidad excluyendo al propio usuario para permitir
        // guardar el mismo valor actual sin falsos conflictos.
        appUserRepository.findByUsername(username)
                .filter(user -> !user.id().equals(currentUser.id()))
                .ifPresent(user -> {
                    throw new UsernameAlreadyExistsException(username);
                });

        if (email != null) {
            appUserRepository.findByEmail(email)
                    .filter(user -> !user.id().equals(currentUser.id()))
                    .ifPresent(user -> {
                        throw new EmailAlreadyExistsException(email);
                    });
        }

        // La actualización conserva contraseña, estado y zona horaria porque
        // este caso de uso solo modifica identidad visible y correo.
        AppUser updatedUser = appUserRepository.save(new AppUser(
                currentUser.id(),
                username,
                displayName,
                email,
                currentUser.timezone(),
                currentUser.passwordHash(),
                currentUser.active(),
                currentUser.createdAt(),
                LocalDateTime.now()
        ));

        return authMapper.toUserResponse(updatedUser);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    private String buildDisplayName(String firstName, String lastName) {
        String normalizedFirstName = firstName.trim();
        String normalizedLastName = lastName == null ? "" : lastName.trim();
        return normalizedLastName.isBlank() ? normalizedFirstName : normalizedFirstName + " " + normalizedLastName;
    }
}
