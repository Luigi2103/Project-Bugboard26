package it.unina.bugboard.service;

import it.unina.bugboard.dto.RichiestaInserimentoIssue;
import it.unina.bugboard.dto.RispostaInserimentoIssue;
import it.unina.bugboard.entity.Issue;
import it.unina.bugboard.repository.InserimentoIssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InserimentoIssueServiceImpl implements InserimentoIssueService {

    private final InserimentoIssueRepository insertIssue;

    @Autowired
    public InserimentoIssueServiceImpl(InserimentoIssueRepository insertIssue) {
        this.insertIssue = insertIssue;
    }

    @Override
    public RispostaInserimentoIssue insert(RichiestaInserimentoIssue richiesta) {
        try {
            // Mapping DTO to Entity
            Issue issue = Issue.builder()
                    .titolo(richiesta.getTitolo())
                    .descrizione(richiesta.getDescrizione())
                    .stato(richiesta.getStato())
                    .priorita(richiesta.getPriorita())
                    .foto(richiesta.getImmagine())
                    .tipologia(richiesta.getTipologia())
                    .dataCreazione(richiesta.getDataCreazione())
                    .idProgetto(richiesta.getIdProgetto())
                    .passiPerRiprodurre(richiesta.getIstruzioni())
                    .richiesta(richiesta.getRichiesta())
                    .titoloDocumento(richiesta.getTitoloDocumento())
                    .descrizioneProblema(richiesta.getDescrizioneDocumento())
                    .richiestaFunzionalita(richiesta.getRichiestaFunzionalita())
                    .idSegnalatore(richiesta.getIdSegnalatore())
                    .build();

            insertIssue.save(issue);
            return new RispostaInserimentoIssue("Issue inserita con successo", true);

        } catch (Exception e) {
            return new RispostaInserimentoIssue("Errore durante l'inserimento: " + e.getMessage(), false);
        }
    }
}
