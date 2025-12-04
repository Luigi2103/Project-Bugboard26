package com.example.projectbugboard26.service;

import com.example.projectbugboard26.DTO.RichiestaLogin;
import com.example.projectbugboard26.DTO.RispostaLogin;
import com.example.projectbugboard26.repository.repositoryUtente;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.projectbugboard26.Entity.User;
import java.util.Optional;

@Service
public class loginServiceImplementation implements LoginService {
    private final repositoryUtente userRepository;
    private final PasswordEncoder passwordEncoder;

    public loginServiceImplementation(repositoryUtente userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RispostaLogin login(RichiestaLogin req) {

        Optional<User> userOpt = userRepository.findByUsernameOrEmail(req.getUsername(), req.getUsername());
        if (userOpt.isEmpty()) {
            return new RispostaLogin(false, "utente non trovato", req.getModalita());
        }

        User user = userOpt.get();

        boolean adminRichiesto = "admin".equalsIgnoreCase(req.getModalita());
        if (user.isAdmin() != adminRichiesto) {
            return new RispostaLogin(false, "modalità non autorizzata", req.getModalita());
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            return new RispostaLogin(false, "password errata", req.getModalita());
        }

        return new RispostaLogin(true, "login eseguito", req.getModalita());
    }
}
