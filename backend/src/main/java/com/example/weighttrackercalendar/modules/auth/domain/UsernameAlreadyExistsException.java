package com.example.weighttrackercalendar.modules.auth.domain;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(String username) {
        super("El nombre de usuario '%s' ya está en uso.".formatted(username));
    }
}
