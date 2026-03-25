package com.example.weighttrackercalendar.modules.auth.domain;

public class CurrentPasswordMismatchException extends RuntimeException {

    public CurrentPasswordMismatchException() {
        super("La contraseña actual no es correcta.");
    }
}
