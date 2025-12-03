package com.example.projectbugboard26.DTO;

public class RispostaLogin {
    private boolean success;
    private String message;
    private String modalita;

    public RispostaLogin(boolean success, String message,String modalita) {
        this.success = success;
        this.message = message;
        this.modalita = modalita;
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

}
