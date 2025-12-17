package it.unina.bugboard.dto.retrieval;

import it.unina.bugboard.dto.model.IssueDTO;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RispostaRecuperoIssue {
    private boolean success;
    private String message;
    private List<IssueDTO> issues;
    private int totalPages;
    private long totalElements;

    public RispostaRecuperoIssue(boolean success, String message, List<IssueDTO> issues) {
        this.success = success;
        this.message = message;
        this.issues = issues;
    }
}