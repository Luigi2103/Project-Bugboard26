package it.unina.bugboard.dto;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RichiestaIssue {
    private String titolo;
    private String descrizione;
    private byte[] immagine;
    private String tipologia;
    private String stato;
    private String priorita;
    private String istruzioni;
    private String richiesta;
    private String titoloDocumento;
    private String descrizioneDocumento;
    private String richiestaFunzionalita;
    private LocalDate dataCreazione;
}
