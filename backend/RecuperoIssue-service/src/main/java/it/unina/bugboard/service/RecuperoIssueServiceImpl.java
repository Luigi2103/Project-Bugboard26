package it.unina.bugboard.service;

import it.unina.bugboard.dto.CommentoDTO;
import it.unina.bugboard.dto.CronologiaDTO;
import it.unina.bugboard.dto.IssueDTO;
import it.unina.bugboard.dto.RichiestaRecuperoIssue;
import it.unina.bugboard.dto.RispostaDettaglioIssue;
import it.unina.bugboard.dto.RispostaRecuperoIssue;
import it.unina.bugboard.entity.Commento;
import it.unina.bugboard.entity.Cronologia;
import it.unina.bugboard.entity.Issue;
import it.unina.bugboard.repository.CommentoRepository;
import it.unina.bugboard.repository.CronologiaRepository;
import it.unina.bugboard.repository.RecuperoIssueRepository;
import it.unina.bugboard.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RecuperoIssueServiceImpl implements RecuperoIssueService {

    private final RecuperoIssueRepository issueRepository;
    private final UtenteRepository utenteRepository;
    private final CommentoRepository commentoRepository;
    private final CronologiaRepository cronologiaRepository;

    @Autowired
    public RecuperoIssueServiceImpl(RecuperoIssueRepository issueRepository,
            UtenteRepository utenteRepository,
            CommentoRepository commentoRepository,
            CronologiaRepository cronologiaRepository) {
        this.issueRepository = issueRepository;
        this.utenteRepository = utenteRepository;
        this.commentoRepository = commentoRepository;
        this.cronologiaRepository = cronologiaRepository;
    }

    @Override
    public RispostaRecuperoIssue recuperaIssue(RichiestaRecuperoIssue richiesta) {
        try {
            int page = richiesta.getPage() != null ? richiesta.getPage() : 0;
            int size = richiesta.getSize() != null ? richiesta.getSize() : 10;

            Sort sort = Sort.unsorted();
            if (richiesta.getSortBy() != null && !richiesta.getSortBy().isEmpty()) {
                Sort.Direction direction = "DESC".equalsIgnoreCase(richiesta.getSortDirection())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;
                sort = Sort.by(direction, richiesta.getSortBy());
            }

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Issue> pageResult;

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

    @Override
    public RispostaDettaglioIssue recuperaDettaglio(Integer idIssue) {
        try {
            Optional<Issue> issueOpt = issueRepository.findById(idIssue);

            if (issueOpt.isEmpty()) {
                return new RispostaDettaglioIssue(false, "Issue non trovata", null, null, null);
            }

            Issue issue = issueOpt.get();
            IssueDTO dto = convertToDTO(issue);
            byte[] foto = issue.getFoto();

            List<Commento> commenti = commentoRepository.findByIdIssueOrderByDataAsc(idIssue);
            List<CommentoDTO> commentiDTO = commenti.stream()
                    .map(this::convertCommentoToDTO)
                    .toList();

            List<Cronologia> cronologiaList = cronologiaRepository.findByIdIssueOrderByDataAsc(idIssue);
            List<CronologiaDTO> cronologiaDTO = cronologiaList.stream()
                    .map(this::convertCronologiaToDTO)
                    .toList();

            RispostaDettaglioIssue risposta = new RispostaDettaglioIssue(true, "Issue recuperata con successo", dto,
                    foto, commentiDTO);
            risposta.setCronologia(cronologiaDTO);
            return risposta;

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
        dto.setDataCreazione(issue.getDataCreazione() != null ? issue.getDataCreazione().atStartOfDay() : null);
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

    private CronologiaDTO convertCronologiaToDTO(Cronologia cronologia) {
        CronologiaDTO dto = new CronologiaDTO();
        dto.setIdCronologia(cronologia.getIdCronologia());
        dto.setData(cronologia.getData());
        dto.setDescrizione(cronologia.getDescrizione());
        if (cronologia.getUtente() != null) {
            dto.setIdUtente(cronologia.getUtente().getIdUtente());
            dto.setNomeUtente(cronologia.getUtente().getNome());
            dto.setCognomeUtente(cronologia.getUtente().getCognome());
        }
        return dto;
    }
}
