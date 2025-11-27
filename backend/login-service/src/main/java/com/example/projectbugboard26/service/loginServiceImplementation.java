package com.example.projectbugboard26.service;

import com.example.projectbugboard26.repository.repositoryUtente;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class loginServiceImplementation implements loginService {
    private final repositoryUtente userRepository;
    private final PasswordEncoder passwordEncoder;

    public loginServiceImplementation(repositoryUtente userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean login(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()))
                .orElse(false);
    }
}
