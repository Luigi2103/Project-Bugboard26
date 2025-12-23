package it.unina.bugboard.dto;

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
    private List<CronologiaDTO> cronologia;

    public RispostaDettaglioIssue(boolean success, String message, IssueDTO issue, byte[] foto,
            List<CommentoDTO> commenti) {
        this.success = success;
        this.message = message;
        this.issue = issue;
        this.foto = foto;
        this.commenti = commenti;
        this.cronologia = null;
    }
}
