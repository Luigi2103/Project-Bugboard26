package it.unina.bugboard.service;

import it.unina.bugboard.dto.RichiestaRecuperoIssue;
import it.unina.bugboard.dto.RispostaRecuperoIssue;
import it.unina.bugboard.dto.RispostaDettaglioIssue;

public interface RecuperoIssueService {
    RispostaRecuperoIssue recuperaIssue(RichiestaRecuperoIssue richiesta);
    RispostaDettaglioIssue recuperaDettaglio(Integer idIssue);
}
