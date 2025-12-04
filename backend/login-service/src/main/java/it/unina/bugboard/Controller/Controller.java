package it.unina.bugboard.Controller;

import it.unina.bugboard.DTO.RichiestaLogin;
import it.unina.bugboard.DTO.RispostaLogin;
import it.unina.bugboard.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class Controller {

    private final LoginService authService;

    public Controller(LoginService authService) {
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


}
