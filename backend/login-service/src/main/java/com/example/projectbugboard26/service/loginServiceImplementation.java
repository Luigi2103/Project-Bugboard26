package com.example.projectbugboard26.service;

import com.example.projectbugboard26.DTO.RichiestaLogin;
import com.example.projectbugboard26.DTO.RispostaLogin;
import com.example.projectbugboard26.repository.repositoryUtente;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.projectbugboard26.Entity.User;
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
    public boolean login(RichiestaLogin loginReq) {

        Optional<User> userOptional = userRepository.findByUsernameOrEmail(loginReq.getUsername(), loginReq.getUsername());

        if (userOptional.isEmpty()) {return false;}

        User user = userOptional.get();


        String modalitaRichiesta = loginReq.getModalita();
        boolean isAdminRichiesto = "admin".equalsIgnoreCase(modalitaRichiesta);

        if (user.isAdmin() != isAdminRichiesto) {return false;}

        return passwordEncoder.matches(loginReq.getPassword(), user.getPasswordHash());
    }
}
