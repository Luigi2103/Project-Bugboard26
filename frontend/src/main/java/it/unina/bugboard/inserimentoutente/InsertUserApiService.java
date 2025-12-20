package it.unina.bugboard.inserimentoutente;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unina.bugboard.inserimentoutente.exception.InsertUserAPIException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class InsertUserApiService {

    private static final String BASE_URL = "http://localhost:8080/api/users";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String BEARER_PREFIX = "Bearer ";

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final it.unina.bugboard.common.SessionManager sessionManager;

    public InsertUserApiService(it.unina.bugboard.common.SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    // REFACTORED: Grouped parameters into data classes
    public static class DatiUtente {
        private final DatiAnagrafici anagrafici;
        private final DatiAccesso accesso;
        private final boolean isAdmin;

        public DatiUtente(DatiAnagrafici anagrafici, DatiAccesso accesso, boolean isAdmin) {
            this.anagrafici = anagrafici;
            this.accesso = accesso;
            this.isAdmin = isAdmin;
        }

        public DatiAnagrafici getAnagrafici() {
            return anagrafici;
        }

        public DatiAccesso getAccesso() {
            return accesso;
        }

        public boolean isAdmin() {
            return isAdmin;
        }
    }

    public static class DatiAnagrafici {
        private final String nome;
        private final String cognome;
        private final String codiceFiscale;
        private final char sesso;
        private final LocalDate dataNascita;

        public DatiAnagrafici(String nome, String cognome, String codiceFiscale, char sesso, LocalDate dataNascita) {
            this.nome = nome;
            this.cognome = cognome;
            this.codiceFiscale = codiceFiscale;
            this.sesso = sesso;
            this.dataNascita = dataNascita;
        }

        public String getNome() {
            return nome;
        }

        public String getCognome() {
            return cognome;
        }

        public String getCodiceFiscale() {
            return codiceFiscale;
        }

        public char getSesso() {
            return sesso;
        }

        public LocalDate getDataNascita() {
            return dataNascita;
        }
    }

    public static class DatiAccesso {
        private final String username;
        private final String password;
        private final String email;

        public DatiAccesso(String username, String password, String email) {
            this.username = username;
            this.password = password;
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getEmail() {
            return email;
        }
    }

    public RispostaInserimentoUser inserisciUtente(DatiUtente datiUtente) {
        try {
            Map<String, Object> insertData = createInsertDataMap(datiUtente);

            String jsonBody = objectMapper.writeValueAsString(insertData);
            String token = this.sessionManager.getToken();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_AUTHORIZATION, BEARER_PREFIX + token)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), RispostaInserimentoUser.class);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new InsertUserAPIException("Errore durante inserimento utente", e);
        }
    }

    private Map<String, Object> createInsertDataMap(DatiUtente datiUtente) {
        Map<String, Object> insertData = new HashMap<>();
        insertData.put("nome", datiUtente.getAnagrafici().getNome());
        insertData.put("cognome", datiUtente.getAnagrafici().getCognome());
        insertData.put("codiceFiscale", datiUtente.getAnagrafici().getCodiceFiscale());
        insertData.put("sesso", String.valueOf(datiUtente.getAnagrafici().getSesso()));
        insertData.put("dataNascita", datiUtente.getAnagrafici().getDataNascita().toString());
        insertData.put("username", datiUtente.getAccesso().getUsername());
        insertData.put("password", datiUtente.getAccesso().getPassword());
        insertData.put("mail", datiUtente.getAccesso().getEmail());
        insertData.put("isAdmin", datiUtente.isAdmin());
        return insertData;
    }
}