package it.unina.bugboard.issuedetails;

import it.unina.bugboard.dto.CommentoDTO;
import it.unina.bugboard.dto.IssueDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RispostaDettaglioIssue {
    private boolean success;
    private String message;
    private IssueDTO issue;
    private byte[] foto;
    private List<CommentoDTO> commenti;
    private List<it.unina.bugboard.dto.CronologiaDTO> cronologia;

    public RispostaDettaglioIssue() {
        // costruttore vuoto
    }

    public List<it.unina.bugboard.dto.CronologiaDTO> getCronologia() {
        return cronologia;
    }

    public void setCronologia(List<it.unina.bugboard.dto.CronologiaDTO> cronologia) {
        this.cronologia = cronologia;
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

    public List<CommentoDTO> getCommenti() {
        return commenti;
    }

    public void setCommenti(List<CommentoDTO> commenti) {
        this.commenti = commenti;
    }
}