package it.unina.bugboard.service.retrieval;

import it.unina.bugboard.dto.retrieval.RichiestaRecuperoIssue;
import it.unina.bugboard.dto.retrieval.RispostaRecuperoIssue;

public interface RecuperoIssueService {
    RispostaRecuperoIssue recuperaIssue(RichiestaRecuperoIssue richiesta);
}