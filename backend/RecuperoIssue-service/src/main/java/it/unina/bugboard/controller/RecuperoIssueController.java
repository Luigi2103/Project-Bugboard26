package it.unina.bugboard.controller;

import it.unina.bugboard.dto.RichiestaRecuperoIssue;
import it.unina.bugboard.dto.RispostaDettaglioIssue;
import it.unina.bugboard.dto.RispostaRecuperoIssue;
import it.unina.bugboard.service.RecuperoIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = "http://localhost:80")
public class RecuperoIssueController {

    private final RecuperoIssueService recuperoIssueService;

    @Autowired
    public RecuperoIssueController(RecuperoIssueService recuperoIssueService) {
        this.recuperoIssueService = recuperoIssueService;
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
        RispostaDettaglioIssue risposta = recuperoIssueService.recuperaDettaglio(id);
        if (risposta.isSuccess()) {
            return ResponseEntity.ok(risposta);
        } else {
            return ResponseEntity.status(404).body(risposta);
        }
    }
}
