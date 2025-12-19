package it.unina.bugboard.controller;

import it.unina.bugboard.dto.RichiestaInserimentoIssue;
import it.unina.bugboard.dto.RispostaInserimentoIssue;
import it.unina.bugboard.service.InserimentoIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = "http://localhost:80")
public class InserimentoIssueController {

    private final InserimentoIssueService inserimentoIssueService;

    @Autowired
    public InserimentoIssueController(InserimentoIssueService inserimentoIssueService) {
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
}
