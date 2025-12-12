package it.unina.bugboard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Gestisce gli errori di validazione dei campi (@Valid)

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        // Raccogli tutti gli errori di validazione
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        // Crea un messaggio generale
        String generalMessage = "Errori di validazione: " +
                fieldErrors.entrySet().stream()
                        .map(e -> e.getKey() + ": " + e.getValue())
                        .collect(Collectors.joining("; "));

        ErrorResponse errorResponse = new ErrorResponse(
                false,
                generalMessage,
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                false,
                "Errore interno del server: " + ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }


    public static class ErrorResponse {
        private boolean success;
        private String message;
        private Map<String, String> fieldErrors;

        public ErrorResponse() {}

        public ErrorResponse(boolean success, String message, Map<String, String> fieldErrors) {
            this.success = success;
            this.message = message;
            this.fieldErrors = fieldErrors;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Map<String, String> getFieldErrors() {
            return fieldErrors;
        }

        public void setFieldErrors(Map<String, String> fieldErrors) {
            this.fieldErrors = fieldErrors;
        }
    }
}