package it.unina.bugboard.Entity;


import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "Utente")
public class Utente extends Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idutente")
    private Long id;
    @Column(nullable = false,unique = true,name="Username")
    private String username;
    @Column(nullable = false,unique = true,name="Password")
    private String password;
    @Column(nullable = false,unique = true,name="Mail")
    private String mail;
    @Column(nullable = false,name="IsAdmin")
    private boolean isAdmin;
    @Column(nullable = true,name="DataInizioRuolo")
    private LocalDate dataInizioRuolo;

    public Utente() {} //serve per JPA

    public Utente(String nome, String cognome, String codiceFiscale, char sesso, LocalDate dataNascita, String username, String password, String mail, boolean isAdmin) {
        super(nome, cognome, codiceFiscale, sesso, dataNascita);
        this.username = username;
        this.password = password;
        this.mail = mail;
        this.isAdmin = isAdmin;

        if(isAdmin)
            this.dataInizioRuolo = LocalDate.now();
    }


    public Long getId() {return id;}



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

}

