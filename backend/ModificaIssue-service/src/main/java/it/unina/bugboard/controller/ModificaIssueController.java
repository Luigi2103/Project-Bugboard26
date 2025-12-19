package it.unina.bugboard.controller;

import it.unina.bugboard.dto.RichiestaModificaIssue;
import it.unina.bugboard.dto.RispostaModificaIssue;
import it.unina.bugboard.service.ModificaIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = "http://localhost:80")
public class ModificaIssueController {

    private final ModificaIssueService modificaIssueService;

    @Autowired
    public ModificaIssueController(ModificaIssueService modificaIssueService) {
        this.modificaIssueService = modificaIssueService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<RispostaModificaIssue> modificaIssue(
            @PathVariable Integer id,
            @RequestBody RichiestaModificaIssue richiesta) {

        RispostaModificaIssue risposta = modificaIssueService.modificaIssue(id, richiesta);
        if (risposta.isSuccess()) {
            return ResponseEntity.ok(risposta);
        } else {
            return ResponseEntity.status(400).body(risposta);
        }
    }
}
