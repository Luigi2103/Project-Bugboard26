package it.unina.bugboard.entity;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "Utente")
public class Utente extends Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idutente")
    private Long id;

    @Column(nullable = false, unique = true, name = "username")
    private String username;

    @Column(nullable = false, unique = true, name = "password")
    private String password;

    @Column(nullable = false, unique = true, name = "mail")
    private String mail;

    @Column(nullable = false, name = "isadmin")
    private boolean isAdmin;

    @Column(nullable = true, name = "datainizioruolo")
    private LocalDate dataInizioRuolo;

    public Utente() {
    } // serve per JPA

    private Utente(Builder builder) {
        super(builder.nome, builder.cognome, builder.codiceFiscale, builder.sesso, builder.dataNascita);
        this.username = builder.username;
        this.password = builder.password;
        this.mail = builder.mail;
        this.isAdmin = builder.isAdmin;
        if (this.isAdmin) {
            this.dataInizioRuolo = LocalDate.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public LocalDate getDataInizioRuolo() {
        return dataInizioRuolo;
    }

    // ===========================
    // Builder
    // ===========================
    public static class Builder {
        // Campi di Persona
        private String nome;
        private String cognome;
        private String codiceFiscale;
        private char sesso;
        private LocalDate dataNascita;

        // Campi Utente
        private String username;
        private String password;
        private String mail;
        private boolean isAdmin;

        public Builder nome(String nome) { this.nome = nome; return this; }
        public Builder cognome(String cognome) { this.cognome = cognome; return this; }
        public Builder codiceFiscale(String codiceFiscale) { this.codiceFiscale = codiceFiscale; return this; }
        public Builder sesso(char sesso) { this.sesso = sesso; return this; }
        public Builder dataNascita(LocalDate dataNascita) { this.dataNascita = dataNascita; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder mail(String mail) { this.mail = mail; return this; }
        public Builder isAdmin(boolean isAdmin) { this.isAdmin = isAdmin; return this; }

        public Utente build() {
            return new Utente(this);
        }
    }
}
