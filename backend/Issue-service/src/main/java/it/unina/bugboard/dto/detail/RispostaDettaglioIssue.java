package it.unina.bugboard.dto.detail;

import it.unina.bugboard.dto.model.CommentoDTO;
import it.unina.bugboard.dto.model.IssueDTO;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RispostaDettaglioIssue {
    private boolean success;
    private String message;
    private IssueDTO issue;
    private byte[] foto;
    private List<CommentoDTO> commenti;
}