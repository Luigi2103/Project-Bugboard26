package it.unina.bugboard.service;

import it.unina.bugboard.dto.IssueDTO;
import it.unina.bugboard.dto.RichiestaDettaglioIssue;
import it.unina.bugboard.dto.RispostaDettaglioIssue;
import it.unina.bugboard.entity.Issue;
import it.unina.bugboard.entity.Tag;
import it.unina.bugboard.repository.RepositoryIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DettaglioIssueServiceImplementation implements DettaglioIssueService {

    private final RepositoryIssueService issueRepository;

    @Autowired
    public DettaglioIssueServiceImplementation(RepositoryIssueService issueRepository) {
        this.issueRepository = issueRepository;
    }

    @Override
    public RispostaDettaglioIssue recuperaDettaglioIssue(RichiestaDettaglioIssue richiesta) {
        try {
            Optional<Issue> issueOpt = issueRepository.findByIdWithTags(richiesta.getIdIssue());

            if (issueOpt.isEmpty()) {
                return new RispostaDettaglioIssue(false, "Issue non trovata", null, null);
            }

            Issue issue = issueOpt.get();
            IssueDTO dto = convertToDTO(issue);
            byte[] foto = issue.getFoto();

            return new RispostaDettaglioIssue(true, "Issue recuperata con successo", dto, foto);

        } catch (Exception e) {
            e.printStackTrace();
            return new RispostaDettaglioIssue(false, "Errore durante il recupero: " + e.getMessage(), null, null);
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
        dto.setDataCreazione(issue.getDataCreazione());
        dto.setIdProgetto(issue.getIdProgetto());
        dto.setIdSegnalatore(issue.getIdSegnalatore());
        dto.setIdAssegnatario(issue.getIdAssegnatario());
        dto.setPassiPerRiprodurre(issue.getPassiPerRiprodurre());
        dto.setRichiesta(issue.getRichiesta());
        dto.setTitoloDocumento(issue.getTitoloDocumento());
        dto.setDescrizioneProblema(issue.getDescrizioneProblema());
        dto.setRichiestaFunzionalita(issue.getRichiestaFunzionalita());
        dto.setHasFoto(issue.getFoto() != null && issue.getFoto().length > 0);

        if (issue.getTags() != null) {
            List<String> tagNames = issue.getTags().stream()
                    .map(Tag::getNome)
                    .collect(Collectors.toList());
            dto.setTags(tagNames);
        }

        return dto;
    }
}