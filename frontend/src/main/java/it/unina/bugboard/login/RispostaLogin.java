package it.unina.bugboard.login;

public class RispostaLogin {
    private boolean success;
    private String message;
    private String modalita;
    private Long idUtente;

    private String token;

    public RispostaLogin() {
    }

    public RispostaLogin(boolean success, String message, String modalita, String token, Long idUtente) {
        this.success = success;
        this.message = message;
        this.modalita = modalita;
        this.token = token;
        this.idUtente = idUtente;
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

    public String getModalita() {
        return modalita;
    }

    public void setModalita(String modalita) {
        this.modalita = modalita;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(Long idUtente) {
        this.idUtente = idUtente;
    }
}
