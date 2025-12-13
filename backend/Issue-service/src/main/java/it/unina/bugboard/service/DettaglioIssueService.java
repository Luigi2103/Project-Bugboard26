package it.unina.bugboard.service;

import it.unina.bugboard.dto.RichiestaDettaglioIssue;
import it.unina.bugboard.dto.RispostaDettaglioIssue;

public interface DettaglioIssueService {
    RispostaDettaglioIssue recuperaDettaglioIssue(RichiestaDettaglioIssue richiesta);
}