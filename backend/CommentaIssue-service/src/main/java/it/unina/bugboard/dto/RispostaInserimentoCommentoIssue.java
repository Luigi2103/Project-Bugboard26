package it.unina.bugboard.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RispostaInserimentoCommentoIssue {

    private boolean success;
    private String message;
    private CommentoDTO commento;

}
