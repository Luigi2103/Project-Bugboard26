package it.unina.bugboard.inserimentoissue.exception;

public class InsertIssueApiException extends RuntimeException {
    public InsertIssueApiException(String message,Throwable cause) {
        super(message,cause);
    }
}
