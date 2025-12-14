package it.unina.bugboard.service;

import it.unina.bugboard.dto.IssueDTO;
import it.unina.bugboard.dto.RichiestaRecuperoIssue;
import it.unina.bugboard.dto.RispostaRecuperoIssue;
import it.unina.bugboard.entity.Issue;
import it.unina.bugboard.entity.Tag;
import it.unina.bugboard.repository.RepositoryIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RecuperoIssueServiceImplementation implements RecuperoIssueService {

    private final RepositoryIssueService issueRepository;

    @Autowired
    public RecuperoIssueServiceImplementation(RepositoryIssueService issueRepository) {
        this.issueRepository = issueRepository;
    }

    @Override
    public RispostaRecuperoIssue recuperaIssue(RichiestaRecuperoIssue richiesta) {
        try {
            List<Issue> issues;

            if (richiesta.getIdProgetto() != null && richiesta.getIdAssegnatario() != null) {

                issues = issueRepository.findByIdProgettoAndIdAssegnatarioAndStatoNot(
                        richiesta.getIdProgetto(),
                        richiesta.getIdAssegnatario());
            } else if (richiesta.getIdProgetto() != null) {

                issues = issueRepository.findByIdProgettoAndStatoNot(richiesta.getIdProgetto());
            } else if (richiesta.getIdAssegnatario() != null) {
                issues = issueRepository.findByIdAssegnatarioAndStatoNot(richiesta.getIdAssegnatario());
            } else {
                return new RispostaRecuperoIssue(false, "Specificare almeno un parametro di ricerca", null);
            }

            List<IssueDTO> issueDTOs = issues.stream()
                    .map(this::convertToDTO)
                    .toList();

            return new RispostaRecuperoIssue(true, "Issue recuperate con successo", issueDTOs);

        } catch (Exception e) {
            e.printStackTrace();
            return new RispostaRecuperoIssue(false, "Errore durante il recupero: " + e.getMessage(), null);
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

        if (issue.getTags() != null) {
            List<String> tagNames = issue.getTags().stream()
                    .map(Tag::getNome)
                    .toList();
            dto.setTags(tagNames);
        }

        return dto;
    }
}