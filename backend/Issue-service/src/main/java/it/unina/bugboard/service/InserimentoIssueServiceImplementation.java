package it.unina.bugboard.service;

import it.unina.bugboard.dto.RichiestaIssue;
import it.unina.bugboard.dto.RispostaIssue;
import it.unina.bugboard.JwtUtils;
import it.unina.bugboard.repository.RepositoryIssueService;

import java.time.LocalDate;

public class InserimentoIssueServiceImplementation implements InserimentoIssueService {

    private final JwtUtils jwtUtils;
    private final String titolo;
    private final String descrizione;
    private final byte[] immagine;
    private final String tipologia;
    private final String stato;
    private final String priorita ;
    private final String istruzioni;
    private final String richiesta;
    private final String titoloDocumento;
    private final String descrizioneDocumento;
    private final String richiestaFunzionalità;
    private final LocalDate dataCreazione;


    public InserimentoIssueServiceImplementation(JwtUtils jwtUtils, String titolo, String descrizione, byte[] immagine, String tipologia, String stato, String priorità, String priorita, String istruzioni, String richiesta, String titoloDocumento, String descrizioneDocumento, String richiestaFunzionalità, LocalDate dataCreazione) {
        this.jwtUtils = jwtUtils;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.immagine = immagine;
        this.tipologia = tipologia;
        this.stato = stato;
        this.priorita = priorita;
        this.istruzioni = istruzioni;
        this.richiesta = richiesta;
        this.titoloDocumento = titoloDocumento;
        this.descrizioneDocumento = descrizioneDocumento;
        this.richiestaFunzionalità = richiestaFunzionalità;
        this.dataCreazione = dataCreazione;
    }


    @Override
    public RispostaIssue Insert(RichiestaIssue risposta) {
        return null;
    }
}
