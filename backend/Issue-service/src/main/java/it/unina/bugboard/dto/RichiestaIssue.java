package it.unina.bugboard.dto;

import java.time.LocalDate;

public class RichiestaIssue {

    private String titolo;
    private String descrizione;
    private byte[] immagine;
    private String tipologia;
    private String stato;
    private String priorita;
    private String istruzioni;
    private String richiesta;
    private String titoloDocumento;
    private String descrizioneDocumento;
    private String richiestaFunzionalità;
    private LocalDate dataCreazione;


    public String getTitolo() {
        return titolo;
    }
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public byte[] getImmagine() {
        return immagine;
    }

    public void setImmagine(byte[] immagine) {
        this.immagine = immagine;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getTipologia() {
        return tipologia;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
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

    public String getIstruzioni() {
        return istruzioni;
    }
    public void setIstruzioni(String istruzioni) {
        this.istruzioni = istruzioni;
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

    public String getDescrizioneDocumento() {
        return descrizioneDocumento;
    }

    public void setDescrizioneDocumento(String descrizioneDocumento) {
        this.descrizioneDocumento = descrizioneDocumento;
    }

    public String getRichiestaFunzionalità() {
        return richiestaFunzionalità;
    }

    public void setRichiestaFunzionalità(String richiestaFunzionalità) {
        this.richiestaFunzionalità = richiestaFunzionalità;
    }

    public LocalDate getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDate dataCreazione) {
        this.dataCreazione = dataCreazione;
    }
}
