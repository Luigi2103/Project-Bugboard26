package it.unina.bugboard.Entity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Persona {
    @Column(nullable = false, name = "nome")
    private String nome;
    @Column(nullable = false, name = "cognome")
    private String cognome;
    @Column(nullable = false, unique = true, name = "cf")
    private String codiceFiscale;
    @Column(nullable = false, name = "sesso")
    private char sesso;
    @Column(nullable = false, name = "datadinascita")
    private LocalDate dataNascita;

    protected Persona(String nome, String cognome, String codiceFiscale, char sesso, LocalDate dataNascita) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.sesso = sesso;
        this.dataNascita = dataNascita;
    }

    protected Persona() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public char getSesso() {
        return sesso;
    }

    public void setSesso(char sesso) {
        this.sesso = sesso;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }
}
