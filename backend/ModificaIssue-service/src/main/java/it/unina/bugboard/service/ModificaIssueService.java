package it.unina.bugboard.service;

import it.unina.bugboard.dto.RichiestaModificaIssue;
import it.unina.bugboard.dto.RispostaModificaIssue;

public interface ModificaIssueService {
    RispostaModificaIssue modificaIssue(Integer id, RichiestaModificaIssue richiesta, Long userId, String nome,
            String cognome);
}
