package it.unina.bugboard.service;

import it.unina.bugboard.dto.IssueDTO;
import it.unina.bugboard.dto.RichiestaModificaIssue;
import it.unina.bugboard.dto.RispostaModificaIssue;
import it.unina.bugboard.entity.Cronologia;
import it.unina.bugboard.entity.Issue;
import it.unina.bugboard.entity.Priorita;
import it.unina.bugboard.repository.CronologiaRepository;
import it.unina.bugboard.repository.ModificaIssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ModificaIssueServiceImpl implements ModificaIssueService {

    private final ModificaIssueRepository repositoryIssueService;
    private final CronologiaRepository cronologiaRepository;

    @Autowired
    public ModificaIssueServiceImpl(ModificaIssueRepository repositoryIssueService,
            CronologiaRepository cronologiaRepository) {
        this.repositoryIssueService = repositoryIssueService;
        this.cronologiaRepository = cronologiaRepository;
    }

    @Override
    public RispostaModificaIssue modificaIssue(Integer id, RichiestaModificaIssue richiesta,
            Long userId, String nome, String cognome) {
        Optional<Issue> issueOptional = repositoryIssueService.findById(id);

        if (issueOptional.isEmpty()) {
            return new RispostaModificaIssue(false, "Issue non trovata", null);
        }

        Issue issue = issueOptional.get();
        String nomeCompleto = buildNomeCompleto(nome, cognome);

        if (richiesta.getStato() != null && richiesta.getStato().isEmpty()) {
            return new RispostaModificaIssue(false, "Stato non valido", null);
        }

        boolean statoModificato = modificaStato(issue, richiesta, id, userId, nomeCompleto);

        RispostaModificaIssue rispostaPriorita = modificaPriorita(issue, richiesta, id, userId, nomeCompleto);
        if (!rispostaPriorita.isSuccess()) {
            return rispostaPriorita;
        }
        boolean prioritaModificata = rispostaPriorita.getIssue() != null;

        return finalizzaModifica(issue, statoModificato || prioritaModificata);
    }

    private String buildNomeCompleto(String nome, String cognome) {
        String nomeCompleto = (nome != null ? nome : "") + " " + (cognome != null ? cognome : "");
        return nomeCompleto.trim();
    }

    private boolean modificaStato(Issue issue, RichiestaModificaIssue richiesta, Integer id, Long userId,
            String nomeCompleto) {
        String nuovoStato = richiesta.getStato();
        if (nuovoStato != null && !nuovoStato.isEmpty() && !nuovoStato.equals(issue.getStato())) {
            issue.setStato(nuovoStato);
            registraCronologia(id, userId, nomeCompleto + " ha modificato lo stato in: " + nuovoStato);
            return true;
        }
        return false;
    }

    private RispostaModificaIssue modificaPriorita(Issue issue, RichiestaModificaIssue richiesta, Integer id,
            Long userId, String nomeCompleto) {
        String nuovaPrioritaStr = richiesta.getPriorita();

        if (nuovaPrioritaStr == null) {
            return new RispostaModificaIssue(true, "", null);
        }

        if (nuovaPrioritaStr.isEmpty()) {
            return new RispostaModificaIssue(false, "Priorità non valida", null);
        }

        try {
            Priorita nuovaPriorita = Priorita.valueOf(nuovaPrioritaStr);
            String vecchiaPriorita = issue.getPriorita() != null ? issue.getPriorita().name() : null;

            if (vecchiaPriorita == null || !nuovaPrioritaStr.equals(vecchiaPriorita)) {
                issue.setPriorita(nuovaPriorita);
                registraCronologia(id, userId,
                        nomeCompleto + " ha modificato la priorità in: " + nuovaPrioritaStr);
                return new RispostaModificaIssue(true, "", mapToDTO(issue));
            }
        } catch (IllegalArgumentException e) {
            return new RispostaModificaIssue(false, "Priorità non valida: " + nuovaPrioritaStr, null);
        }

        return new RispostaModificaIssue(true, "", null);
    }

    private RispostaModificaIssue finalizzaModifica(Issue issue, boolean modified) {
        if (modified) {
            Issue updatedIssue = repositoryIssueService.save(issue);
            return new RispostaModificaIssue(true, "Issue modificata con successo", mapToDTO(updatedIssue));
        }
        return new RispostaModificaIssue(true, "Nessuna modifica effettuata", mapToDTO(issue));
    }

    private void registraCronologia(Integer idIssue, Long idUtente, String descrizione) {
        Cronologia cronologia = Cronologia.builder()
                .idIssue(idIssue)
                .idUtente(idUtente)
                .descrizione(descrizione)
                .build();
        cronologiaRepository.save(cronologia);
    }

    private IssueDTO mapToDTO(Issue issue) {
        IssueDTO dto = new IssueDTO();
        dto.setIdIssue(issue.getIdIssue());
        dto.setTitolo(issue.getTitolo());
        dto.setDescrizione(issue.getDescrizione());
        dto.setStato(issue.getStato());
        dto.setPriorita(issue.getPriorita() != null ? issue.getPriorita().name() : null);
        dto.setTipologia(issue.getTipologia() != null ? issue.getTipologia().name() : null);
        if (issue.getDataCreazione() != null) {
            dto.setDataCreazione(issue.getDataCreazione().atStartOfDay());
        }
        dto.setIdProgetto(issue.getIdProgetto());
        dto.setIdSegnalatore(issue.getIdSegnalatore());
        dto.setIdAssegnatario(issue.getIdAssegnatario());
        dto.setPassiPerRiprodurre(issue.getPassiPerRiprodurre());
        dto.setRichiesta(issue.getRichiesta());
        dto.setTitoloDocumento(issue.getTitoloDocumento());
        dto.setDescrizioneProblema(issue.getDescrizioneProblema());
        dto.setRichiestaFunzionalita(issue.getRichiestaFunzionalita());
        return dto;
    }
}