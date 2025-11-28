package com.example.projectbugboard26.Entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "Utente")
public class Utente extends Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "mail")
    private String email;

    @Column(unique = true, nullable = false, name = "username")
    private String username;

    @Column(nullable = false, name = "password")
    private String passwordHash;

    @Column(nullable = false, name = "isadmin")
    private boolean isAdmin = false;

    public Utente() {
    }

    public Utente(String nome, String cognome, String codiceFiscale, char sesso, Date dataDiNascita, Long id,
                  String email, String username, String passwordHash, boolean isAdmin) {
        super(nome, cognome, codiceFiscale, sesso, dataDiNascita);
        this.id = id;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.isAdmin = isAdmin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
