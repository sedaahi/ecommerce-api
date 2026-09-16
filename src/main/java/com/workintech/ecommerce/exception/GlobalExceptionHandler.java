package com.workintech.ecommerce.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Bizim oluşturduğumuz API hatalarını yakalar.
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, String>> handleApiException(
            ApiException exception
    ) {
        Map<String, String> response = new HashMap<>();
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(exception.getStatus())
                .body(response);
    }

    // DTO validation hatalarını yakalar.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> response = new HashMap<>();

        String message = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Validation failed.");

        response.put("message", message);

        return ResponseEntity
                .badRequest()
                .body(response);
    }
}