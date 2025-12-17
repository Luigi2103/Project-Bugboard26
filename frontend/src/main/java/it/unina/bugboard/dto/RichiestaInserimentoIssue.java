package it.unina.bugboard.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RichiestaInserimentoIssue {
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
    private String dataCreazione;
    private int idProgetto;
    private int idSegnalatore;
}
