package it.unina.bugboard.controller;

import it.unina.bugboard.dto.RichiestaLogin;
import it.unina.bugboard.dto.RichiestaUpdate;
import it.unina.bugboard.dto.RispostaLogin;
import it.unina.bugboard.dto.RispostaUpdate;
import it.unina.bugboard.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class
AuthController {

    private final LoginService authService;

    public AuthController(LoginService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<RispostaLogin> login(@RequestBody RichiestaLogin login) {

        RispostaLogin risposta = authService.login(login);

        if (risposta.isSuccess()) {
            return ResponseEntity.ok(risposta);
        }

        return ResponseEntity.status(401).body(risposta);
    }

    @PutMapping("/update")
    public ResponseEntity<RispostaUpdate> update(@RequestBody RichiestaUpdate update) {
        RispostaUpdate risposta = authService.update(update);

        if (risposta.isSuccess()) {
            return ResponseEntity.ok(risposta);
        }

        return ResponseEntity.status(401).body(risposta);
    }

}
