package com.example.projectbugboard26.Controller;

import com.example.projectbugboard26.DTO.RichiestaLogin;
import com.example.projectbugboard26.DTO.RispostaLogin;
import com.example.projectbugboard26.service.LoginService;
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
