package it.unina.bugboard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Commento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Commento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdCommento")
    private Integer idCommento;

    @Column(name = "Testo", nullable = false, columnDefinition = "TEXT")
    private String testo;

    @Column(name = "Data", nullable = false, insertable = false, updatable = false)
    private LocalDateTime data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdIssue", nullable = false)
    private Issue issue;

    @Column(name = "IdUtente", nullable = false)
    private Integer idUtente;
}
