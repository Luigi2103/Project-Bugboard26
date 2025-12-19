package it.unina.bugboard.controller;

import it.unina.bugboard.dto.comment.*;
import it.unina.bugboard.dto.detail.*;
import it.unina.bugboard.dto.insertion.*;
import it.unina.bugboard.dto.retrieval.*;
import it.unina.bugboard.service.comment.CommentaIssueService;
import it.unina.bugboard.service.detail.DettaglioIssueService;
import it.unina.bugboard.service.retrieval.RecuperoIssueService;
import it.unina.bugboard.service.insertion.InserimentoIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final RecuperoIssueService recuperoIssueService;
    private final DettaglioIssueService dettaglioIssueService;
    private final CommentaIssueService inserimentoCommentoService;
    private final InserimentoIssueService inserimentoIssueService;

    @Autowired
    public IssueController(RecuperoIssueService recuperoIssueService, DettaglioIssueService dettaglioIssueService,
            CommentaIssueService inserimentoCommentoService, InserimentoIssueService inserimentoIssueService) {
        this.recuperoIssueService = recuperoIssueService;
        this.dettaglioIssueService = dettaglioIssueService;
        this.inserimentoCommentoService = inserimentoCommentoService;
        this.inserimentoIssueService = inserimentoIssueService;
    }

    @PostMapping
    public ResponseEntity<RispostaInserimentoIssue> inserisciIssue(@RequestBody RichiestaInserimentoIssue richiesta) {
        RispostaInserimentoIssue risposta = inserimentoIssueService.insert(richiesta);

        if (risposta.isSuccess()) {
            return ResponseEntity.status(201).body(risposta);
        } else {
            return ResponseEntity.status(400).body(risposta);
        }
    }

    @GetMapping
    public ResponseEntity<RispostaRecuperoIssue> recuperaIssue(@ModelAttribute RichiestaRecuperoIssue richiesta) {
        RispostaRecuperoIssue risposta = recuperoIssueService.recuperaIssue(richiesta);
        if (risposta.isSuccess()) {
            return ResponseEntity.ok(risposta);
        } else {
            return ResponseEntity.status(400).body(risposta);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RispostaDettaglioIssue> dettaglioIssue(@PathVariable Integer id) {
        RichiestaDettaglioIssue richiesta = new RichiestaDettaglioIssue(id);
        RispostaDettaglioIssue risposta = dettaglioIssueService.recuperaDettaglioIssue(richiesta);
        if (risposta.isSuccess()) {
            return ResponseEntity.ok(risposta);
        } else {
            return ResponseEntity.status(404).body(risposta);
        }
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<RispostaInserimentoCommentoIssue> inserisciCommento(
            @PathVariable Integer id,
            @RequestBody RichiestaInserimentoCommentoIssue richiesta,
            @RequestHeader("X-User-Id") Long userId) {

        richiesta.setIdIssue(id);
        RispostaInserimentoCommentoIssue risposta = inserimentoCommentoService.inserisciCommento(richiesta, userId);
        if (risposta.isSuccess()) {
            return ResponseEntity.status(201).body(risposta);
        } else {
            return ResponseEntity.status(400).body(risposta);
        }
    }
}