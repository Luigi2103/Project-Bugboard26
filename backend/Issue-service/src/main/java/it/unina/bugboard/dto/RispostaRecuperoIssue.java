package it.unina.bugboard.dto;

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
}