package it.unina.bugboard.dto;

import java.util.List;

public class IssueDTO {
    private Integer idIssue;
    private String titolo;
    private String descrizione;
    private String stato;
    private String priorita;
    private String tipologia;
    private String dataCreazione;
    private Integer idProgetto;
    private Integer idSegnalatore;
    private Integer idAssegnatario;
    private String passiPerRiprodurre;
    private String richiesta;
    private String titoloDocumento;
    private String descrizioneProblema;
    private String richiestaFunzionalita;
    private boolean hasFoto;

    // New fields
    private String nomeAssegnatario;
    private String cognomeAssegnatario;

    private List<String> tags;

    public Integer getIdIssue() {
        return idIssue;
    }

    public void setIdIssue(Integer idIssue) {
        this.idIssue = idIssue;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getPriorita() {
        return priorita;
    }

    public void setPriorita(String priorita) {
        this.priorita = priorita;
    }

    public String getTipologia() {
        return tipologia;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    public String getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(String dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public Integer getIdProgetto() {
        return idProgetto;
    }

    public void setIdProgetto(Integer idProgetto) {
        this.idProgetto = idProgetto;
    }

    public Integer getIdSegnalatore() {
        return idSegnalatore;
    }

    public void setIdSegnalatore(Integer idSegnalatore) {
        this.idSegnalatore = idSegnalatore;
    }

    public Integer getIdAssegnatario() {
        return idAssegnatario;
    }

    public void setIdAssegnatario(Integer idAssegnatario) {
        this.idAssegnatario = idAssegnatario;
    }

    public String getPassiPerRiprodurre() {
        return passiPerRiprodurre;
    }

    public void setPassiPerRiprodurre(String passiPerRiprodurre) {
        this.passiPerRiprodurre = passiPerRiprodurre;
    }

    public String getRichiesta() {
        return richiesta;
    }

    public void setRichiesta(String richiesta) {
        this.richiesta = richiesta;
    }

    public String getTitoloDocumento() {
        return titoloDocumento;
    }

    public void setTitoloDocumento(String titoloDocumento) {
        this.titoloDocumento = titoloDocumento;
    }

    public String getDescrizioneProblema() {
        return descrizioneProblema;
    }

    public void setDescrizioneProblema(String descrizioneProblema) {
        this.descrizioneProblema = descrizioneProblema;
    }

    public String getRichiestaFunzionalita() {
        return richiestaFunzionalita;
    }

    public void setRichiestaFunzionalita(String richiestaFunzionalita) {
        this.richiestaFunzionalita = richiestaFunzionalita;
    }

    public boolean isHasFoto() {
        return hasFoto;
    }

    public void setHasFoto(boolean hasFoto) {
        this.hasFoto = hasFoto;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getNomeAssegnatario() {
        return nomeAssegnatario;
    }

    public void setNomeAssegnatario(String nomeAssegnatario) {
        this.nomeAssegnatario = nomeAssegnatario;
    }

    public String getCognomeAssegnatario() {
        return cognomeAssegnatario;
    }

    public void setCognomeAssegnatario(String cognomeAssegnatario) {
        this.cognomeAssegnatario = cognomeAssegnatario;
    }
}
