package it.unina.bugboard.service;

import it.unina.bugboard.dto.RichiestaIssue;
import it.unina.bugboard.dto.RispostaIssue;

public interface InserimentoIssueService {
    RispostaIssue Insert(RichiestaIssue risposta);
}
