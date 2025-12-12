package it.unina.bugboard.service;

import it.unina.bugboard.dto.RichiestaRecuperoIssue;
import it.unina.bugboard.dto.RispostaRecuperoIssue;

public interface RecuperoIssueService {
    RispostaRecuperoIssue recuperaIssue(RichiestaRecuperoIssue richiesta);
}