package it.unina.bugboard.inserimentoIssue.exception;

public class InsertIssueApiException extends RuntimeException {
    public InsertIssueApiException(String message,Throwable cause) {
        super(message,cause);
    }
}
