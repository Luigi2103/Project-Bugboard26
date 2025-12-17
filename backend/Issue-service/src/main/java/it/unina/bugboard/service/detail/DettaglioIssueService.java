package it.unina.bugboard.service.detail;

import it.unina.bugboard.dto.detail.RichiestaDettaglioIssue;
import it.unina.bugboard.dto.detail.RispostaDettaglioIssue;

public interface DettaglioIssueService {
    RispostaDettaglioIssue recuperaDettaglioIssue(RichiestaDettaglioIssue richiesta);
}