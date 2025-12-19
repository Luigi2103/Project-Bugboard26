package it.unina.bugboard.service;

import it.unina.bugboard.dto.CommentoDTO;
import it.unina.bugboard.dto.RichiestaInserimentoCommentoIssue;
import it.unina.bugboard.dto.RispostaInserimentoCommentoIssue;
import it.unina.bugboard.entity.Commento;
import it.unina.bugboard.entity.Issue;
import it.unina.bugboard.entity.Utente;
import it.unina.bugboard.repository.CommentoRepository;
import it.unina.bugboard.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CommentaIssueServiceImpl implements CommentaIssueService {

    private final CommentoRepository commentoRepository;
    private final IssueRepository issueRepository;

    @Autowired
    public CommentaIssueServiceImpl(CommentoRepository commentoRepository,
                                    IssueRepository issueRepository) {
        this.commentoRepository = commentoRepository;
        this.issueRepository = issueRepository;
    }

    @Override
    public RispostaInserimentoCommentoIssue inserisciCommento(RichiestaInserimentoCommentoIssue richiesta, Long userId) {

        RispostaInserimentoCommentoIssue richiestaValida = isRichiestaValida(richiesta, userId);

        if (richiestaValida != null)
            return richiestaValida;

        Commento nuovoCommento = new Commento();
        nuovoCommento.setTesto(richiesta.getTesto().trim());
        nuovoCommento.setIdIssue(richiesta.getIdIssue());

        Utente utente = new Utente();
        utente.setIdUtente(userId);
        nuovoCommento.setUtente(utente);

        Commento commentoSalvato = commentoRepository.save(nuovoCommento);

        Optional<Commento> commentoCompleto = commentoRepository.findById(commentoSalvato.getIdCommento());

        if (commentoCompleto.isPresent()) {
            CommentoDTO dto = convertToDTO(commentoCompleto.get());
            return new RispostaInserimentoCommentoIssue(true, "Commento inserito con successo", dto);
        }

        return new RispostaInserimentoCommentoIssue(true, "Commento inserito con successo", null);
    }

    private RispostaInserimentoCommentoIssue isRichiestaValida(RichiestaInserimentoCommentoIssue richiesta, Long userId) {
        if (richiesta.getIdIssue() == null) {
            return new RispostaInserimentoCommentoIssue(false, "ID Issue è obbligatorio", null);
        }

        if (userId == null) {
            return new RispostaInserimentoCommentoIssue(false, "Utente non autenticato", null);
        }

        if (richiesta.getTesto() == null || richiesta.getTesto().trim().isEmpty()) {
            return new RispostaInserimentoCommentoIssue(false, "Il testo del commento non può essere vuoto", null);
        }

        Optional<Issue> issueOpt = issueRepository.findById(richiesta.getIdIssue());
        if (issueOpt.isEmpty()) {
            return new RispostaInserimentoCommentoIssue(false, "Issue non trovata", null);
        }

        Issue issue = issueOpt.get();

        if ("CLOSED".equals(issue.getStato())) {
            return new RispostaInserimentoCommentoIssue(false, "Non è possibile commentare una Issue chiusa", null);
        }
        return null;
    }

    private CommentoDTO convertToDTO(Commento commento) {
        CommentoDTO dto = new CommentoDTO();
        dto.setIdCommento(commento.getIdCommento());
        dto.setTesto(commento.getTesto());
        dto.setData(commento.getData());
        dto.setIdIssue(commento.getIdIssue());

        if (commento.getUtente() != null) {
            dto.setIdUtente(commento.getUtente().getIdUtente());
            dto.setNomeUtente(commento.getUtente().getNome());
            dto.setCognomeUtente(commento.getUtente().getCognome());
        }

        return dto;
    }
}
