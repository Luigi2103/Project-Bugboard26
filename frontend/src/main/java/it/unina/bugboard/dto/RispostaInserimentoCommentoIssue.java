package it.unina.bugboard.dto;

public class RispostaInserimentoCommentoIssue {

    private boolean success;
    private String message;
    private CommentoDTO commento;

    public RispostaInserimentoCommentoIssue() {
    }

    public RispostaInserimentoCommentoIssue(boolean success, String message, CommentoDTO commento) {
        this.success = success;
        this.message = message;
        this.commento = commento;
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

    public CommentoDTO getCommento() {
        return commento;
    }

    public void setCommento(CommentoDTO commento) {
        this.commento = commento;
    }
}
