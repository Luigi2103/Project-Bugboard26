package it.unina.bugboard.dto;

public class RispostaLogin {
    private boolean success;
    private String message;
    private String modalita;

    private String token;

    public RispostaLogin(boolean success, String message, String modalita, String token) {
        this.success = success;
        this.message = message;
        this.modalita = modalita;
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getModalita() {
        return modalita;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
