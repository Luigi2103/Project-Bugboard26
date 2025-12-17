package it.unina.bugboard.service.insertion;

import it.unina.bugboard.dto.insertion.RichiestaInserimentoIssue;
import it.unina.bugboard.dto.insertion.RispostaInserimentoIssue;

public interface InserimentoIssueService {
    RispostaInserimentoIssue insert(RichiestaInserimentoIssue risposta);
}
