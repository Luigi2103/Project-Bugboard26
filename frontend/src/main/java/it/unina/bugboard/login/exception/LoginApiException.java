package it.unina.bugboard.login.exception;

public class LoginApiException extends RuntimeException {
    public LoginApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

