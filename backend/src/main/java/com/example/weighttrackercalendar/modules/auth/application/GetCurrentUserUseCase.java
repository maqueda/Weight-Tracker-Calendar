package com.example.weighttrackercalendar.modules.auth.application;

import com.example.weighttrackercalendar.modules.auth.api.AuthUserResponse;
import com.example.weighttrackercalendar.modules.auth.domain.AppUserRepository;
import com.example.weighttrackercalendar.shared.application.CurrentUserProvider;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentUserUseCase {

    private final CurrentUserProvider currentUserProvider;
    private final AppUserRepository appUserRepository;
    private final AuthMapper authMapper;

    public GetCurrentUserUseCase(
            CurrentUserProvider currentUserProvider,
            AppUserRepository appUserRepository,
            AuthMapper authMapper
    ) {
        this.currentUserProvider = currentUserProvider;
        this.appUserRepository = appUserRepository;
        this.authMapper = authMapper;
    }

    public AuthUserResponse handle() {
        return appUserRepository.findById(currentUserProvider.getCurrentUserId())
                .map(authMapper::toUserResponse)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario autenticado."));
    }
}
