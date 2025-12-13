package it.unina.bugboard.issuedetails;

public class RichiestaDettaglioIssue {
    private Integer idIssue;

    public RichiestaDettaglioIssue() {
    }

    public RichiestaDettaglioIssue(Integer idIssue) {
        this.idIssue = idIssue;
    }

    public Integer getIdIssue() {
        return idIssue;
    }

    public void setIdIssue(Integer idIssue) {
        this.idIssue = idIssue;
    }
}