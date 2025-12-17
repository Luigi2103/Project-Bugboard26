package it.unina.bugboard.service;

import it.unina.bugboard.dto.IssueDTO;
import it.unina.bugboard.dto.RichiestaRecuperoIssue;
import it.unina.bugboard.dto.RispostaRecuperoIssue;
import it.unina.bugboard.entity.Issue;

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
    public RecuperoIssueServiceImplementation(RepositoryIssueService issueRepository,
            it.unina.bugboard.repository.RepositoryUtente utenteRepository) {
        this.issueRepository = issueRepository;
        this.utenteRepository = utenteRepository;
    }

    private final it.unina.bugboard.repository.RepositoryUtente utenteRepository;

    @Override
    public RispostaRecuperoIssue recuperaIssue(RichiestaRecuperoIssue richiesta) {
        try {
            int page = richiesta.getPage() != null ? richiesta.getPage() : 0;
            int size = richiesta.getSize() != null ? richiesta.getSize() : 10;
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page,
                    size);

            org.springframework.data.domain.Page<Issue> pageResult;

            if (richiesta.getIdProgetto() != null && richiesta.getIdAssegnatario() != null) {
                pageResult = issueRepository.findByIdProgettoAndIdAssegnatarioAndStatoNot(
                        richiesta.getIdProgetto(),
                        richiesta.getIdAssegnatario(),
                        pageable);
            } else if (richiesta.getIdProgetto() != null) {
                pageResult = issueRepository.findByIdProgettoAndStatoNot(richiesta.getIdProgetto(), pageable);
            } else if (richiesta.getIdAssegnatario() != null) {
                pageResult = issueRepository.findByIdAssegnatarioAndStatoNot(richiesta.getIdAssegnatario(), pageable);
            } else {
                return new RispostaRecuperoIssue(false, "Specificare almeno un parametro di ricerca", null);
            }

            List<IssueDTO> issueDTOs = pageResult.getContent().stream()
                    .map(this::convertToDTO)
                    .toList();

            RispostaRecuperoIssue response = new RispostaRecuperoIssue(true, "Issue recuperate con successo",
                    issueDTOs);
            response.setTotalPages(pageResult.getTotalPages());
            response.setTotalElements(pageResult.getTotalElements());
            return response;

        } catch (Exception e) {
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

        if (issue.getIdAssegnatario() != null) {
            utenteRepository.findById(issue.getIdAssegnatario().longValue()).ifPresent(u -> {
                dto.setNomeAssegnatario(u.getNome());
                dto.setCognomeAssegnatario(u.getCognome());
            });
        }

        return dto;
    }
}