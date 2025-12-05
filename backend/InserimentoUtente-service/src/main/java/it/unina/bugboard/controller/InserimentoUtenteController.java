package it.unina.bugboard.controller;

import it.unina.bugboard.Service.InserimentoUtenteService;
import it.unina.bugboard.dto.RichiestaInserimentoUtente;
import it.unina.bugboard.dto.RispostaInserimentoUtente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inserimento-utente")
public class InserimentoUtenteController {

    private final InserimentoUtenteService inserimentoUtenteService;

    @Autowired
    public InserimentoUtenteController(InserimentoUtenteService inserimentoUtenteService) {
        this.inserimentoUtenteService = inserimentoUtenteService;
    }

    @PostMapping
    public ResponseEntity<RispostaInserimentoUtente> inserisciUtente(
            @RequestBody RichiestaInserimentoUtente richiesta) {
        RispostaInserimentoUtente risposta = inserimentoUtenteService.InserisciUtente(richiesta);

        if (risposta != null && risposta.isSuccess()) {
            return ResponseEntity.ok(risposta);
        } else {
            return ResponseEntity.badRequest().body(risposta);
        }
    }
}
