package com.example.projectbugboard26.service;

import com.example.projectbugboard26.repository.repositoryUtente;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.projectbugboard26.Entity.Utente;
import java.util.Optional;

@Service
public class loginServiceImplementation implements loginService {
    private final repositoryUtente userRepository;
    private final PasswordEncoder passwordEncoder;

    public loginServiceImplementation(repositoryUtente userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean login(String username, String rawPassword, String modalita) {
        Optional<Utente> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return false;
        }

        Utente user = userOpt.get();
        boolean isModeAdmin = modalita.equalsIgnoreCase("admin");
        if (isModeAdmin != user.isAdmin()) {
            return false;
        }

        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }
}
