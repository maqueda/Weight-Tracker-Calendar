package com.example.weighttrackercalendar.shared.application;

import com.example.weighttrackercalendar.modules.auth.application.AuthenticatedUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;

        if (principal instanceof AuthenticatedUser authenticatedUser) {
            return authenticatedUser.id();
        }

        throw new IllegalStateException("No hay usuario autenticado en el contexto de seguridad.");
    }
}

