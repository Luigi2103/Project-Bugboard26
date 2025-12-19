package it.unina.bugboard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Commento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Commento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcommento")
    private Long idCommento;

    @Column(name = "testo", nullable = false)
    private String testo;

    @Column(name = "data", nullable = false, insertable = false, updatable = false)
    private LocalDate data;

    @Column(name = "idissue", nullable = false)
    private Integer idIssue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idutente", nullable = false)
    private Utente utente;
}
