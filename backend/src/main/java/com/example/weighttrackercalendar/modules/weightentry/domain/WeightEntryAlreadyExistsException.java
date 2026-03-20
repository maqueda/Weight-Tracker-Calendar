package com.example.weighttrackercalendar.modules.weightentry.domain;

public class WeightEntryAlreadyExistsException extends RuntimeException {

    public WeightEntryAlreadyExistsException(String message) {
        super(message);
    }
}

