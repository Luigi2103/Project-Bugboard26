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
@Builder
public class Cronologia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcronologia")
    private Long idCronologia;

    @Column(name = "data", nullable = false, insertable = false, updatable = false)
    private LocalDateTime data;

    @Column(name = "descrizione", columnDefinition = "TEXT")
    private String descrizione;

    @Column(name = "idutente", nullable = false)
    private Long idUtente;

    @Column(name = "idissue", nullable = false)
    private Integer idIssue;
}
