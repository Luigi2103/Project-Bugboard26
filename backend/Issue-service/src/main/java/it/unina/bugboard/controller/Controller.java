package it.unina.bugboard.controller;

import it.unina.bugboard.dto.RichiestaDettaglioIssue;
import it.unina.bugboard.dto.RichiestaRecuperoIssue;
import it.unina.bugboard.dto.RispostaDettaglioIssue;
import it.unina.bugboard.dto.RispostaRecuperoIssue;
import it.unina.bugboard.service.DettaglioIssueService;
import it.unina.bugboard.service.RecuperoIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/issue")
public class Controller {

    private final RecuperoIssueService recuperoIssueService;
    private final DettaglioIssueService dettaglioIssueService;

    @Autowired
    public Controller(RecuperoIssueService recuperoIssueService, DettaglioIssueService dettaglioIssueService) {
        this.recuperoIssueService = recuperoIssueService;
        this.dettaglioIssueService = dettaglioIssueService;
    }

    @PostMapping("/recupera")
    public ResponseEntity<RispostaRecuperoIssue> recuperaIssue(@RequestBody RichiestaRecuperoIssue richiesta) {
        RispostaRecuperoIssue risposta = recuperoIssueService.recuperaIssue(richiesta);
        if (risposta.isSuccess()) {
            return ResponseEntity.ok(risposta);
        } else {
            return ResponseEntity.status(400).body(risposta);
        }
    }

    @PostMapping("/dettaglio")
    public ResponseEntity<RispostaDettaglioIssue> dettaglioIssue(@RequestBody RichiestaDettaglioIssue richiesta) {
        RispostaDettaglioIssue risposta = dettaglioIssueService.recuperaDettaglioIssue(richiesta);
        if (risposta.isSuccess()) {
            return ResponseEntity.ok(risposta);
        } else {
            return ResponseEntity.status(404).body(risposta);
        }
    }
}