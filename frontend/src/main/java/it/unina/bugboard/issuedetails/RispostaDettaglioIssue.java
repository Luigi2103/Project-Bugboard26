package it.unina.bugboard.issuedetails;

import it.unina.bugboard.dto.IssueDTO;

public class RispostaDettaglioIssue {
    private boolean success;
    private String message;
    private IssueDTO issue;
    private byte[] foto;

    public RispostaDettaglioIssue() {
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

    public IssueDTO getIssue() {
        return issue;
    }

    public void setIssue(IssueDTO issue) {
        this.issue = issue;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }
}