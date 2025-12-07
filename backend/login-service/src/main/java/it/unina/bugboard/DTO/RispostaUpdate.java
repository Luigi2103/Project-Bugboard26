package it.unina.bugboard.dto;

public class RispostaUpdate {
    private String message;
    private boolean success;

    public RispostaUpdate(String message, boolean success) {

        this.message = message;
        this.success = success;

    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
