package com.example.projectbugboard26.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Utente")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String codiceFiscale;

    @Column(nullable = false)
    private char sesso;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean isAdmin = false;

    //---------------------------------------COSTRUTTORI---------------------------------------------
    public Utente(Long id, String nome, String cognome, String email, String codiceFiscale, char sesso, String username, String passwordHash, boolean isAdmin) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.codiceFiscale = codiceFiscale;
        this.sesso = sesso;
        this.username = username;
        this.passwordHash = passwordHash;
        this.isAdmin = isAdmin;
    }

    public Utente() {} //Serve necessariamente per JPA


    //--------------------------GETTER & SETTER-------------------------------------------------------
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}

    public String getCognome() {return cognome;}
    public void setCognome(String cognome) {this.cognome = cognome;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getCodiceFiscale() {return codiceFiscale;}
    public void setCodiceFiscale(String codiceFiscale) {this.codiceFiscale = codiceFiscale;}

    public char getSesso() {return sesso;}
    public void setSesso(char sesso) {this.sesso = sesso;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getPasswordHash() {return passwordHash;}
    public void setPasswordHash(String passwordHash) {this.passwordHash = passwordHash;}

    public boolean isAdmin() {return isAdmin;}
    public void setAdmin(boolean admin) {isAdmin = admin;}

}

