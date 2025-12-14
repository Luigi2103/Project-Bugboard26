package it.unina.bugboard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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
    @Column(name = "idissue")
    private Integer idIssue;

    @Column(name = "titolo", nullable = false, length = 200)
    private String titolo;

    @Column(name = "descrizione", nullable = false, columnDefinition = "TEXT")
    private String descrizione;

    @Column(name = "stato", nullable = false)
    private String stato;

    @Enumerated(EnumType.STRING)
    @Column(name = "priorita")
    private Priorita priorita;

    @Column(name = "foto")
    private byte[] foto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipologia", nullable = false)
    private Tipologia tipologia;

    @Column(name = "datacreazione", nullable = false, insertable = false, updatable = false)
    private LocalDate dataCreazione;

    @Column(name = "idprogetto", nullable = false)
    private Integer idProgetto;

    @Column(name = "idsegnalatore", nullable = false)
    private Integer idSegnalatore;

    @Column(name = "idassegnatario")
    private Integer idAssegnatario;

    @Column(name = "passiperriprodurre", columnDefinition = "TEXT")
    private String passiPerRiprodurre;

    @Column(name = "richiesta", columnDefinition = "TEXT")
    private String richiesta;

    @Column(name = "titolodocumento", columnDefinition = "TEXT")
    private String titoloDocumento;

    @Column(name = "descrizioneproblema", columnDefinition = "TEXT")
    private String descrizioneProblema;

    @Column(name = "richiestafunzionalita", columnDefinition = "TEXT")
    private String richiestaFunzionalita;

    @ManyToMany
    @JoinTable(name = "issue_tag", joinColumns = @JoinColumn(name = "idissue"), inverseJoinColumns = @JoinColumn(name = "idtag"))
    private Set<Tag> tags;
}
