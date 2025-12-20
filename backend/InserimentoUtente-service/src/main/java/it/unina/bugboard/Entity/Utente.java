package it.unina.bugboard.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = "Utente")
public class Utente extends Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idutente")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false, unique = true, name = "username")
    private String username;

    @Column(nullable = false, name = "password")
    private String password;

    @Column(nullable = false, unique = true, name = "mail")
    private String mail;

    @Column(nullable = false, name = "isadmin")
    private boolean isAdmin;

    @Column(nullable = true, name = "datainizioruolo")
    @Setter(AccessLevel.NONE)
    private LocalDate dataInizioRuolo;

    @PrePersist
    protected void onCreate() {
        if (this.isAdmin) {
            this.dataInizioRuolo = LocalDate.now();
        }
    }
}