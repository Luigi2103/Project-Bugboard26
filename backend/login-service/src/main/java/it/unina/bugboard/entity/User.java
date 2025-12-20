package it.unina.bugboard.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor


@Entity
@Table(name = "Utente")
public class User extends BasicUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idutente")
    private Long id;

    @Column(nullable = false, name = "mail")
    private String email;

    @Column(nullable = false, name = "isadmin")
    @JsonProperty("isAdmin")
    private boolean isAdmin;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cognome")
    private String cognome;


    public User(String username, String passwordHash, String email, boolean isAdmin) {
        super(username, passwordHash);
        this.email = email;
        this.isAdmin = isAdmin;
    }
}