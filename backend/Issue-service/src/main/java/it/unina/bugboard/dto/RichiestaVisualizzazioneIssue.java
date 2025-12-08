package it.unina.bugboard.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RichiestaVisualizzazioneIssue {
    private String titolo;
    private String stato;
    private String priorita;
    private String tipologia;
    private Integer idProgetto;
    private Integer idAssegnatario;
}
