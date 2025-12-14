package it.unina.bugboard.dto;

import java.time.LocalDate;

public class CommentoDTO {
    private Long idCommento;
    private String testo;
    private LocalDate data;
    private Integer idIssue;
    private Long idUtente;
    private String nomeUtente;
    private String cognomeUtente;

    public CommentoDTO() {
    }

    public Long getIdCommento() {
        return idCommento;
    }

    public void setIdCommento(Long idCommento) {
        this.idCommento = idCommento;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Integer getIdIssue() {
        return idIssue;
    }

    public void setIdIssue(Integer idIssue) {
        this.idIssue = idIssue;
    }

    public Long getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(Long idUtente) {
        this.idUtente = idUtente;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public String getCognomeUtente() {
        return cognomeUtente;
    }

    public void setCognomeUtente(String cognomeUtente) {
        this.cognomeUtente = cognomeUtente;
    }
}
