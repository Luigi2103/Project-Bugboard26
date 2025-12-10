package it.unina.bugboard.dto;

public class RispostaLogin {
    private boolean success;
    private String message;
    private String modalita;
    private Long id_utente;

    private String token;

    public RispostaLogin(boolean success, String message, String modalita, String token, Long id_utente) {
        this.success = success;
        this.message = message;
        this.modalita = modalita;
        this.token = token;
        this.id_utente = id_utente;
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

    public Long getIdUtente() {
        return id_utente;
    }

    public void setIdUtente(Long id_utente) {
        this.id_utente = id_utente;
    }

}
