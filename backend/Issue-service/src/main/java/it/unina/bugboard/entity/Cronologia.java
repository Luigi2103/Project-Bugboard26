package it.unina.bugboard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Cronologia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cronologia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdCronologia")
    private Integer idCronologia;

    @Column(name = "Data", nullable = false, insertable = false, updatable = false)
    private LocalDateTime data;

    @Column(name = "Descrizione", columnDefinition = "TEXT")
    private String descrizione;

    @Column(name = "IdUtente", nullable = false)
    private Integer idUtente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdIssue", nullable = false)
    private Issue issue;
}
