package com.example.weighttrackercalendar.modules.auth.domain;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("Ya existe una cuenta registrada con el correo " + email + ".");
    }
}
