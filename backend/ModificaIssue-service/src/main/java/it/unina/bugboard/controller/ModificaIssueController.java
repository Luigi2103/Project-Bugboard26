package it.unina.bugboard.controller;

import it.unina.bugboard.JwtUtils;
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
    private final JwtUtils jwtUtils;

    @Autowired
    public ModificaIssueController(ModificaIssueService modificaIssueService, JwtUtils jwtUtils) {
        this.modificaIssueService = modificaIssueService;
        this.jwtUtils = jwtUtils;
    }

    @PutMapping("/{id}")
    public ResponseEntity<RispostaModificaIssue> modificaIssue(@PathVariable Integer id,
            @RequestBody RichiestaModificaIssue richiesta, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtils.extractUserId(token);
        String nome = jwtUtils.extractClaim(token, claims -> claims.get("nome", String.class));
        String cognome = jwtUtils.extractClaim(token, claims -> claims.get("cognome", String.class));

        RispostaModificaIssue risposta = modificaIssueService.modificaIssue(id, richiesta, userId, nome, cognome);
        if (risposta.isSuccess()) {
            return ResponseEntity.ok(risposta);
        } else {
            return ResponseEntity.status(400).body(risposta);
        }
    }
}
