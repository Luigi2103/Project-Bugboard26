package it.unina.bugboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RispostaModificaIssue {
    private boolean success;
    private String message;
    private IssueDTO issue;
}
