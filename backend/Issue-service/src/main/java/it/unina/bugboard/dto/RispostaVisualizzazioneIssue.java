package it.unina.bugboard.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RispostaVisualizzazioneIssue {
    private Integer id;
    private String titolo;
    private String descrizione;
    private String stato;
    private String priorita;
    private String tipologia;
    private LocalDateTime dataCreazione;
    private Integer idProgetto;
    private Integer idSegnalatore;
    private Integer idAssegnatario;

    private List<String> tags;
    private byte[] foto;


    private String passiPerRiprodurre;
    private String richiesta;
    private String titoloDocumento;
    private String descrizioneProblema;
    private String richiestaFunzionalita;
}
