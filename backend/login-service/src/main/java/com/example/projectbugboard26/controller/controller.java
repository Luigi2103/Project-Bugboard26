package com.example.projectbugboard26.controller;

import com.example.projectbugboard26.service.loginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class controller {

    private final loginService authService;

    public controller(loginService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {

        boolean success = authService.login(username, password);

        if (success) {
            return ResponseEntity.ok("Login ok");
        }

        return ResponseEntity.status(401).body("Credenziali non valide");
    }
}
