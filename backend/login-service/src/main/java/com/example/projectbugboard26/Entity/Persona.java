package com.example.projectbugboard26.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.util.Date;

@MappedSuperclass
public abstract class Persona {

    @Column(nullable = false , name = "Nome")
    private String nome;

    @Column(nullable = false, name = "Cognome")
    private String cognome;

    @Column(nullable = false, unique = true, name = "CF")
    private String codiceFiscale;

    @Column(nullable = false,name="Sesso")
    private char sesso;

    @Column(nullable = false,name = "DataDiNascita")
    private Date dataDiNascita;


    public Persona() {}
    public Persona(String nome, String cognome, String codiceFiscale, char sesso, Date dataDiNascita) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.sesso = sesso;
        this.dataDiNascita = dataDiNascita;
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

    public Date getDataDiNascita() {
        return dataDiNascita;
    }
    public void setDataDiNascita(Date dataDiNascita) {
        this.dataDiNascita = dataDiNascita;
    }


}






