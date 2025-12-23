package it.unina.bugboard.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CronologiaDTO {
    private Long idCronologia;
    private LocalDateTime data;
    private String descrizione;
    private Long idUtente;
    private String nomeUtente;
    private String cognomeUtente;
}
