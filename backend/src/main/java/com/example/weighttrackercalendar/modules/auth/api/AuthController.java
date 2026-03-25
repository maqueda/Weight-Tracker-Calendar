package com.example.weighttrackercalendar.modules.auth.api;

import com.example.weighttrackercalendar.modules.auth.application.GetCurrentUserUseCase;
import com.example.weighttrackercalendar.modules.auth.application.LoginUseCase;
import com.example.weighttrackercalendar.modules.auth.application.RegisterUserUseCase;
import com.example.weighttrackercalendar.modules.auth.application.ChangePasswordUseCase;
import com.example.weighttrackercalendar.modules.auth.application.UpdateProfileUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;

    public AuthController(
            RegisterUserUseCase registerUserUseCase,
            LoginUseCase loginUseCase,
            GetCurrentUserUseCase getCurrentUserUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            UpdateProfileUseCase updateProfileUseCase
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterUserRequest request) {
        return registerUserUseCase.handle(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return loginUseCase.handle(request);
    }

    @GetMapping("/me")
    public AuthUserResponse me() {
        return getCurrentUserUseCase.handle();
    }

    @PutMapping("/profile")
    public AuthUserResponse updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return updateProfileUseCase.handle(request);
    }

    @PostMapping("/change-password")
    public SimpleMessageResponse changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return changePasswordUseCase.handle(request);
    }
}
