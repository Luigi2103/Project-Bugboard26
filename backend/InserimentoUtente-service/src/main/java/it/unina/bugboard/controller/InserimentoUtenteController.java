package it.unina.bugboard.controller;

import it.unina.bugboard.service.InserimentoUtenteService;
import it.unina.bugboard.dto.RichiestaInserimentoUtente;
import it.unina.bugboard.dto.RispostaInserimentoUtente;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inserimentoUtente")
public class InserimentoUtenteController {

    private final InserimentoUtenteService inserimentoUtenteService;

    @Autowired
    public InserimentoUtenteController(InserimentoUtenteService inserimentoUtenteService) {
        this.inserimentoUtenteService = inserimentoUtenteService;
    }

    @PostMapping
    public ResponseEntity<RispostaInserimentoUtente> inserisciUtente(
            @Valid @RequestBody RichiestaInserimentoUtente richiesta) {

        RispostaInserimentoUtente risposta = inserimentoUtenteService.inserisciUtente(richiesta);

        if (risposta.isSuccess()) {
            return ResponseEntity.status(201).body(risposta);  // 201 Created
        } else {
            return ResponseEntity.status(400).body(risposta);  // 400 Bad Request
        }
    }
}
