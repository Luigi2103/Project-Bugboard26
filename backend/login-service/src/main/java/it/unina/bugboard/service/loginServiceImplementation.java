package it.unina.bugboard.service;

import it.unina.bugboard.DTO.RichiestaLogin;
import it.unina.bugboard.DTO.RispostaLogin;
import it.unina.bugboard.repository.repositoryUtente;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.unina.bugboard.Entity.User;
import java.util.Optional;

@Service
public class loginServiceImplementation implements LoginService {
    private final repositoryUtente userRepository;
    private final PasswordEncoder passwordEncoder;
    private final it.unina.bugboard.JwtUtils jwtUtils;

    public loginServiceImplementation(repositoryUtente userRepository, PasswordEncoder passwordEncoder,
            it.unina.bugboard.JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public RispostaLogin login(RichiestaLogin req) {

        Optional<User> userOpt = userRepository.findByUsernameOrEmail(req.getUsername(), req.getUsername());
        if (userOpt.isEmpty()) {
            return new RispostaLogin(false, "utente non trovato", req.getModalita(), null);
        }

        User user = userOpt.get();

        boolean adminRichiesto = "admin".equalsIgnoreCase(req.getModalita());
        if (user.isAdmin() != adminRichiesto) {
            return new RispostaLogin(false, "modalità non autorizzata", req.getModalita(), null);
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            return new RispostaLogin(false, "password errata", req.getModalita(), null);
        }

        String token = jwtUtils.generateToken(user.getUsername(), user.isAdmin() ? "ADMIN" : "USER");
        return new RispostaLogin(true, "login eseguito", req.getModalita(), token);
    }
}
