package com.example.weighttrackercalendar.modules.auth.application;

import com.example.weighttrackercalendar.modules.auth.api.AuthResponse;
import com.example.weighttrackercalendar.modules.auth.api.LoginRequest;
import com.example.weighttrackercalendar.modules.auth.domain.AppUser;
import com.example.weighttrackercalendar.modules.auth.domain.AppUserRepository;
import com.example.weighttrackercalendar.modules.auth.domain.InvalidCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUseCase {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    public LoginUseCase(
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

    public AuthResponse handle(LoginRequest request) {
        AppUser appUser = appUserRepository.findByUsername(request.username().trim())
                .orElseThrow(InvalidCredentialsException::new);

        if (!appUser.active() || !passwordEncoder.matches(request.password(), appUser.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        return authMapper.toAuthResponse(jwtService.generateToken(appUser), appUser);
    }
}
