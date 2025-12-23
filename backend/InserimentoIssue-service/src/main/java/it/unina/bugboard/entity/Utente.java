package it.unina.bugboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Utente")
@Getter
@Setter
@NoArgsConstructor
public class Utente {

    @Id
    @Column(name = "idutente")
    private Long idUtente;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cognome")
    private String cognome;
}
