package it.unina.bugboard.service;

import it.unina.bugboard.dto.RichiestaInserimentoIssue;
import it.unina.bugboard.dto.RispostaInserimentoIssue;

public interface InserimentoIssueService {
    RispostaInserimentoIssue insert(RichiestaInserimentoIssue richiesta);
}
