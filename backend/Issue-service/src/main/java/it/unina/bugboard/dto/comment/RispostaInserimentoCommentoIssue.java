package it.unina.bugboard.dto.comment;

import it.unina.bugboard.dto.model.CommentoDTO;
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
