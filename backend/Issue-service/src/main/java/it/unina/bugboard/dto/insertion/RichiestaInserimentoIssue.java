package it.unina.bugboard.dto.insertion;

import java.time.LocalDate;

import it.unina.bugboard.entity.Priorita;
import it.unina.bugboard.entity.Tipologia;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RichiestaInserimentoIssue {
    private String titolo;
    private String descrizione;
    private byte[] immagine;
    private Tipologia tipologia;
    private String stato;
    private Priorita priorita;
    private String istruzioni;
    private String richiesta;
    private String titoloDocumento;
    private String descrizioneDocumento;
    private String richiestaFunzionalita;
    private LocalDate dataCreazione;
    private int idProgetto;
    private int idSegnalatore;
}
