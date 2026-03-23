package com.example.weighttrackercalendar.shared.api;

import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntryAlreadyExistsException;
import com.example.weighttrackercalendar.modules.weightentry.domain.WeightEntryNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(WeightEntryAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleAlreadyExists(WeightEntryAlreadyExistsException exception) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(WeightEntryNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(WeightEntryNotFoundException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiErrorResponse> handleBadRequest(Exception exception) {
        // Centraliza errores funcionales y de validación para que el frontend
        // reciba mensajes consistentes.
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ApiErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), message));
    }
}

