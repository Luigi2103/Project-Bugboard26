package it.unina.bugboard.service;

import it.unina.bugboard.dto.RichiestaLogin;
import it.unina.bugboard.dto.RichiestaUpdate;
import it.unina.bugboard.dto.RispostaLogin;
import it.unina.bugboard.dto.RispostaUpdate;
import it.unina.bugboard.repository.RepositoryUtente;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.unina.bugboard.entity.User;
import java.util.Optional;

@Service
public class LoginServiceImplementation implements LoginService {
    private final RepositoryUtente userRepository;
    private final PasswordEncoder passwordEncoder;
    private final it.unina.bugboard.JwtUtils jwtUtils;

    public LoginServiceImplementation(RepositoryUtente userRepository, PasswordEncoder passwordEncoder,
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

    @Override
    public RispostaUpdate update(RichiestaUpdate updateReq) {

        Optional<User> userOpt = userRepository.findByUsername(updateReq.getUsername());
        if (userOpt.isEmpty()) {
            return new RispostaUpdate("utente non trovato", false);
        }

        User user = userOpt.get();

        user.setPasswordHash(passwordEncoder.encode(updateReq.getPassword()));

        userRepository.save(user);

        return new RispostaUpdate("Password Cambiata Con Successo", true);
    }

}
