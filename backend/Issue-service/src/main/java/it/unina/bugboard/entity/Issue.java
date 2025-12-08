package it.unina.bugboard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Issue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdIssue")
    private Integer idIssue;

    @Column(name = "Titolo", nullable = false, length = 200)
    private String titolo;

    @Column(name = "Descrizione", nullable = false, columnDefinition = "TEXT")
    private String descrizione;

    @Enumerated(EnumType.STRING)
    @Column(name = "Stato", nullable = false) // Postgres custom enum needs careful handling, but usually STRING works
                                              // if types match
    private Stato stato;

    @Enumerated(EnumType.STRING)
    @Column(name = "Priorita")
    private Priorita priorita;

    @Column(name = "Foto")
    private byte[] foto;

    @Enumerated(EnumType.STRING)
    @Column(name = "Tipologia", nullable = false)
    private Tipologia tipologia;

    @Column(name = "DataCreazione", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dataCreazione;

    @Column(name = "IdProgetto", nullable = false)
    private Integer idProgetto;

    @Column(name = "IdSegnalatore", nullable = false)
    private Integer idSegnalatore;

    @Column(name = "IdAssegnatario")
    private Integer idAssegnatario;

    @Column(name = "PassiPerRiprodurre", columnDefinition = "TEXT")
    private String passiPerRiprodurre;

    @Column(name = "Richiesta", columnDefinition = "TEXT")
    private String richiesta;

    @Column(name = "TitoloDocumento", columnDefinition = "TEXT")
    private String titoloDocumento;

    @Column(name = "DescrizioneProblema", columnDefinition = "TEXT")
    private String descrizioneProblema;

    @Column(name = "RichiestaFunzionalita", columnDefinition = "TEXT")
    private String richiestaFunzionalita;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commento> commenti;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cronologia> cronologia;

    @ManyToMany
    @JoinTable(name = "Issue_TAG", joinColumns = @JoinColumn(name = "IdIssue"), inverseJoinColumns = @JoinColumn(name = "IdTag"))
    private Set<Tag> tags;
}
