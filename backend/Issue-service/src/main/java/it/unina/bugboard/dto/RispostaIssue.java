package it.unina.bugboard.dto;

public class RispostaIssue {

    private String message;
    private boolean success;

    public RispostaIssue(String message, boolean success) {

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
