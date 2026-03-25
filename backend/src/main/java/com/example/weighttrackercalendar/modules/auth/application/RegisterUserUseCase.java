package com.example.weighttrackercalendar.modules.auth.application;

import com.example.weighttrackercalendar.modules.auth.api.AuthResponse;
import com.example.weighttrackercalendar.modules.auth.api.RegisterUserRequest;
import com.example.weighttrackercalendar.modules.auth.domain.AppUser;
import com.example.weighttrackercalendar.modules.auth.domain.AppUserRepository;
import com.example.weighttrackercalendar.modules.auth.domain.UsernameAlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegisterUserUseCase {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    public RegisterUserUseCase(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthMapper authMapper
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authMapper = authMapper;
    }

    public AuthResponse handle(RegisterUserRequest request) {
        if (appUserRepository.existsByUsername(request.username().trim())) {
            throw new UsernameAlreadyExistsException(request.username());
        }

        LocalDateTime now = LocalDateTime.now();
        AppUser savedUser = appUserRepository.save(new AppUser(
                null,
                request.username().trim(),
                request.displayName().trim(),
                normalizeEmail(request.email()),
                "Europe/Madrid",
                passwordEncoder.encode(request.password()),
                true,
                now,
                now
        ));

        return authMapper.toAuthResponse(jwtService.generateToken(savedUser), savedUser);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim().toLowerCase();
    }
}
