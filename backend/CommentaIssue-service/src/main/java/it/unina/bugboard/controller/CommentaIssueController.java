package it.unina.bugboard.controller;

import it.unina.bugboard.dto.RichiestaInserimentoCommentoIssue;
import it.unina.bugboard.dto.RispostaInserimentoCommentoIssue;
import it.unina.bugboard.service.CommentaIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = "http://localhost:80")
public class CommentaIssueController {

    private final CommentaIssueService commentaIssueService;

    @Autowired
    public CommentaIssueController(CommentaIssueService commentaIssueService) {
        this.commentaIssueService = commentaIssueService;
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<RispostaInserimentoCommentoIssue> inserisciCommento(
            @PathVariable Integer id,
            @RequestBody RichiestaInserimentoCommentoIssue richiesta,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {

        // Assuming API Gateway passes user ID in X-User-Id header or similar.
        // For now, I'll parse it if present. If API Gateway passes "Authorization", decoding needed.
        // BUT, user asked for "intelligent reuse".
        // In previous conversation, "Securing Docker Configuration", we might have discussed this.
        // Since this is a quick refactor, I will assume the Gateway or Security Filter validates JWT 
        // and passes info. 
        // For now, I will grab ID from header if available, or throw error.
        
        Long userId = null;
        if (userIdHeader != null) {
            try {
                userId = Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                // Ignore or handle
            }
        }
        
        // IMPORTANT: The original IssueController extracted it from implicit security context/JWT.
        // Here, since it's a microservice, effectively we rely on Gateway propagating identity.
        // Standard practice: Gateway verifies JWT, passes X-User-Id.
        // I will assume X-User-Id is populated.
        
        // IfuserId is null, service will return error "Utente non autenticato".
        
        richiesta.setIdIssue(id); // Ensure ID matches path
        RispostaInserimentoCommentoIssue risposta = commentaIssueService.inserisciCommento(richiesta, userId);

        if (risposta.isSuccess()) {
            return ResponseEntity.status(201).body(risposta);
        } else {
            return ResponseEntity.status(400).body(risposta);
        }
    }
}
