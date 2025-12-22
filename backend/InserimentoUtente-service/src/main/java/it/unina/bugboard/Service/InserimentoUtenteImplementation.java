package it.unina.bugboard.service;

import it.unina.bugboard.entity.Utente;
import it.unina.bugboard.dto.RichiestaInserimentoUtente;
import it.unina.bugboard.dto.RispostaInserimentoUtente;
import it.unina.bugboard.repository.RepositoryInserimentoUtente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class InserimentoUtenteImplementation implements InserimentoUtenteService {

    private final RepositoryInserimentoUtente inserimentoUtente;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public InserimentoUtenteImplementation(RepositoryInserimentoUtente utenteRepository,
                                           PasswordEncoder passwordEncoder) {
        this.inserimentoUtente = utenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RispostaInserimentoUtente inserisciUtente(RichiestaInserimentoUtente richiesta) {

        // Controllo duplicati
        if (inserimentoUtente.existsByUsername(richiesta.getUsername())) {
            return new RispostaInserimentoUtente(false, "Username già in uso");
        }
        if (inserimentoUtente.existsByMail(richiesta.getMail())) {
            return new RispostaInserimentoUtente(false, "Email già registrata");
        }
        if (inserimentoUtente.existsByCodiceFiscale(richiesta.getCodiceFiscale())) {
            return new RispostaInserimentoUtente(false, "Codice fiscale già registrato");
        }

        try {
            Utente nuovoUtente = Utente.builder()
                    .nome(richiesta.getNome())
                    .cognome(richiesta.getCognome())
                    .codiceFiscale(richiesta.getCodiceFiscale().toUpperCase())
                    .sesso(richiesta.getSesso().toUpperCase().charAt(0))
                    .dataNascita(richiesta.getDataNascita())
                    .username(richiesta.getUsername())
                    .password(passwordEncoder.encode(richiesta.getPassword()))
                    .mail(richiesta.getMail().toLowerCase())
                    .isAdmin(richiesta.isAdmin())
                    .build();

            inserimentoUtente.save(nuovoUtente);
            return new RispostaInserimentoUtente(true, "Utente inserito con successo");

        } catch (DataIntegrityViolationException e) {
            return new RispostaInserimentoUtente(false, "Dati duplicati: verifica username, email e codice fiscale");
        } catch (Exception e) {
            return new RispostaInserimentoUtente(false, "Errore durante l'inserimento: " + e.getMessage());
        }
    }
}