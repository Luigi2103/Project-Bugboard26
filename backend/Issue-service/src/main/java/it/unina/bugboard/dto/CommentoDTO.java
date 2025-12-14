package it.unina.bugboard.dto;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor

public class CommentoDTO {
    private Long idCommento;
    private String testo;
    private LocalDate data;
    private Integer idIssue;
    private Long idUtente;
    private String nomeUtente;
    private String cognomeUtente;

}