package com.chatbot.exception;

import com.chatbot.dto.SimpleChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the ChatBot application
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        System.err.println("Validation error: " + errors);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SimpleChatResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        System.err.println("Invalid argument: " + ex.getMessage());
        return ResponseEntity.badRequest()
                .body(SimpleChatResponse.error("Invalid request: " + ex.getMessage(), "INVALID_ARGUMENT"));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<SimpleChatResponse> handleIllegalStateException(IllegalStateException ex) {
        System.err.println("Service unavailable: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(SimpleChatResponse.error("Service temporarily unavailable", "SERVICE_UNAVAILABLE"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SimpleChatResponse> handleGenericException(Exception ex) {
        System.err.println("Unexpected error occurred: " + ex.getMessage());
        return ResponseEntity.internalServerError()
                .body(SimpleChatResponse.error("An unexpected error occurred", "INTERNAL_ERROR"));
    }
}