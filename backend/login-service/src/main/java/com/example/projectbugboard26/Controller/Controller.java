package com.example.projectbugboard26.Controller;

import com.example.projectbugboard26.DTO.RichiestaLogin;
import com.example.projectbugboard26.DTO.RispostaLogin;
import com.example.projectbugboard26.service.loginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class Controller {

    private final loginService authService;

    public Controller(loginService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<RispostaLogin> login(@RequestBody RichiestaLogin login) {
        boolean success = authService.login(login);

        if (success) {
            return ResponseEntity.ok(new RispostaLogin(true,"login eseguito",login.getModalita()));
        }

        return ResponseEntity.status(401).body(new RispostaLogin(false,"errore login",login.getModalita()));
    }

}
