package it.unina.bugboard.dto;

public class RichiestaInserimentoCommentoIssue {

    private Integer idIssue;
    private String testo;

    public RichiestaInserimentoCommentoIssue() {
    }

    public RichiestaInserimentoCommentoIssue(Integer idIssue, String testo) {
        this.idIssue = idIssue;
        this.testo = testo;
    }

    public Integer getIdIssue() {
        return idIssue;
    }

    public void setIdIssue(Integer idIssue) {
        this.idIssue = idIssue;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }
}
