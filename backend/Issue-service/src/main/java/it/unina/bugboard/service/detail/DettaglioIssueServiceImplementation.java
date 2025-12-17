package it.unina.bugboard.service.detail;

import it.unina.bugboard.dto.model.CommentoDTO;
import it.unina.bugboard.dto.model.IssueDTO;
import it.unina.bugboard.dto.detail.RichiestaDettaglioIssue;
import it.unina.bugboard.dto.detail.RispostaDettaglioIssue;
import it.unina.bugboard.entity.Commento;
import it.unina.bugboard.entity.Issue;

import it.unina.bugboard.repository.RepositoryCommento;
import it.unina.bugboard.repository.RepositoryIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DettaglioIssueServiceImplementation implements DettaglioIssueService {

    private final RepositoryIssueService issueRepository;
    private final RepositoryCommento commentoRepository;

    @Autowired
    public DettaglioIssueServiceImplementation(RepositoryIssueService issueRepository,
            RepositoryCommento commentoRepository) {
        this.issueRepository = issueRepository;
        this.commentoRepository = commentoRepository;
    }

    @Override
    public RispostaDettaglioIssue recuperaDettaglioIssue(RichiestaDettaglioIssue richiesta) {
        try {
            Optional<Issue> issueOpt = issueRepository.findById(richiesta.getIdIssue());

            if (issueOpt.isEmpty()) {
                return new RispostaDettaglioIssue(false, "Issue non trovata", null, null, null);
            }

            Issue issue = issueOpt.get();
            IssueDTO dto = convertToDTO(issue);
            byte[] foto = issue.getFoto();

            List<Commento> commenti = commentoRepository.findByIdIssueOrderByDataAsc(richiesta.getIdIssue());
            List<CommentoDTO> commentiDTO = commenti.stream()
                    .map(this::convertCommentoToDTO)
                    .toList();

            return new RispostaDettaglioIssue(true, "Issue recuperata con successo", dto, foto, commentiDTO);

        } catch (Exception e) {
            return new RispostaDettaglioIssue(false, "Errore durante il recupero: " + e.getMessage(), null, null, null);
        }
    }

    private IssueDTO convertToDTO(Issue issue) {
        IssueDTO dto = new IssueDTO();
        dto.setIdIssue(issue.getIdIssue());
        dto.setTitolo(issue.getTitolo());
        dto.setDescrizione(issue.getDescrizione());
        dto.setStato(issue.getStato());
        dto.setPriorita(issue.getPriorita() != null ? issue.getPriorita().name() : null);
        dto.setTipologia(issue.getTipologia().name());
        dto.setDataCreazione(issue.getDataCreazione().atStartOfDay());
        dto.setIdProgetto(issue.getIdProgetto());
        dto.setIdSegnalatore(issue.getIdSegnalatore());
        dto.setIdAssegnatario(issue.getIdAssegnatario());
        dto.setPassiPerRiprodurre(issue.getPassiPerRiprodurre());
        dto.setRichiesta(issue.getRichiesta());
        dto.setTitoloDocumento(issue.getTitoloDocumento());
        dto.setDescrizioneProblema(issue.getDescrizioneProblema());
        dto.setRichiestaFunzionalita(issue.getRichiestaFunzionalita());
        dto.setHasFoto(issue.getFoto() != null && issue.getFoto().length > 0);

        return dto;
    }

    private CommentoDTO convertCommentoToDTO(Commento commento) {
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