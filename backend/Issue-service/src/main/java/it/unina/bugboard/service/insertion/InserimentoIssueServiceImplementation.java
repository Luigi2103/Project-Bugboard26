package it.unina.bugboard.service.insertion;

import it.unina.bugboard.dto.insertion.RichiestaInserimentoIssue;
import it.unina.bugboard.dto.insertion.RispostaInserimentoIssue;
import it.unina.bugboard.entity.Issue;
import it.unina.bugboard.repository.RepositoryInserimentoIssue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InserimentoIssueServiceImplementation implements InserimentoIssueService {

    private final RepositoryInserimentoIssue insertIssue;

    @Autowired
    public InserimentoIssueServiceImplementation(RepositoryInserimentoIssue insertIssue) {
        this.insertIssue = insertIssue;
    }

    @Override
    public RispostaInserimentoIssue insert(RichiestaInserimentoIssue richiesta) {

        try {
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
            return new RispostaInserimentoIssue("Issue non inserita con successo", false);
        }

    }
}
