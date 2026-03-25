package com.example.weighttrackercalendar.modules.auth.domain;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Usuario o contraseña incorrectos.");
    }
}
