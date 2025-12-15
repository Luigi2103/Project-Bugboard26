package it.unina.bugboard.service;

import it.unina.bugboard.dto.RichiestaInserimentoIssue;
import it.unina.bugboard.dto.RispostaInserimentoIssue;
import it.unina.bugboard.entity.Issue;
import it.unina.bugboard.entity.Priorita;
import it.unina.bugboard.entity.Tag;
import it.unina.bugboard.entity.Tipologia;
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

        try{
            Issue issue = new Issue(richiesta.getTitolo(), richiesta.getDescrizione(),richiesta.getStato(),
                    richiesta.getPriorita(),richiesta.getImmagine(),
                    richiesta.getTipologia(), richiesta.getDataCreazione(),
                    richiesta.getIdProgetto(),richiesta.getIstruzioni(),richiesta.getRichiesta(),
                    richiesta.getTitoloDocumento(),richiesta.getDescrizione(), richiesta.getRichiestaFunzionalita(),
                    richiesta.getIdSegnalator(), richiesta.getTags());


        insertIssue.save(issue);
        return new RispostaInserimentoIssue("Issue inserita con successo",true);

        }catch (Exception e){
            return new RispostaInserimentoIssue("Issue non inserita con successo",false);
        }

    }
}
