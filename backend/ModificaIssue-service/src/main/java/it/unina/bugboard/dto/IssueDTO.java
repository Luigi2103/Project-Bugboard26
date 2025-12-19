package it.unina.bugboard.dto;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IssueDTO {
    private Integer idIssue;
    private String titolo;
    private String descrizione;
    private String stato;
    private String priorita;
    private String tipologia;
    private LocalDateTime dataCreazione;
    private Integer idProgetto;
    private Integer idSegnalatore;
    private Integer idAssegnatario;
    private String passiPerRiprodurre;
    private String richiesta;
    private String titoloDocumento;
    private String descrizioneProblema;
    private String richiestaFunzionalita;
    private boolean hasFoto;
}
