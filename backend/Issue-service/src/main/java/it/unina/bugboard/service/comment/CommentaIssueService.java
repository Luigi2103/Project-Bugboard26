package it.unina.bugboard.service.comment;

import it.unina.bugboard.dto.comment.RichiestaInserimentoCommentoIssue;
import it.unina.bugboard.dto.comment.RispostaInserimentoCommentoIssue;

public interface CommentaIssueService {
    RispostaInserimentoCommentoIssue inserisciCommento(RichiestaInserimentoCommentoIssue richiesta, Long userId);
}