package it.unina.bugboard.Service;

import it.unina.bugboard.Entity.Utente;
import it.unina.bugboard.dto.RichiestaInserimentoUtente;
import it.unina.bugboard.dto.RispostaInserimentoUtente;
import it.unina.bugboard.repository.RepositoryInserimentoUtente;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class InserimentoUtenteImplementation implements InserimentoUtenteService {

    private final it.unina.bugboard.repository.RepositoryInserimentoUtente inserimentoUtente;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Autowired
    public InserimentoUtenteImplementation(RepositoryInserimentoUtente utenteRepository, PasswordEncoder passwordEncoder) {
        this.inserimentoUtente = utenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RispostaInserimentoUtente InserisciUtente(RichiestaInserimentoUtente richiesta) {

        // Controllo duplicati
        if (inserimentoUtente.existsByUsername(richiesta.getUsername())) {
            return new RispostaInserimentoUtente(false, "Username già in uso");
        }
        if (inserimentoUtente.existsByMail(richiesta.getMail())) {
            return new RispostaInserimentoUtente(false, "Email già registrata");
        }

        try {
            // Creazione Utente
            Utente nuovoUtente = new it.unina.bugboard.Entity.Utente(
                    richiesta.getNome(),
                    richiesta.getCognome(),
                    richiesta.getCodiceFiscale(),
                    richiesta.getSesso(),
                    richiesta.getDataNascita(),
                    richiesta.getUsername(),
                    passwordEncoder.encode(richiesta.getPassword()), // Hash password
                    richiesta.getMail(),
                    richiesta.isAdmin());

            inserimentoUtente.save(nuovoUtente);
            return new RispostaInserimentoUtente(true, "Utente inserito con successo");

        } catch (Exception e) {
            return new RispostaInserimentoUtente(false, "Errore durante l'inserimento: " + e.getMessage());
        }
    }
}
