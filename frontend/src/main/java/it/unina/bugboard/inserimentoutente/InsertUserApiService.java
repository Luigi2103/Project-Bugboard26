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

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RispostaInserimentoUser inserisciUtente(String nome, String cognome, String codiceFiscale,
                                                   char sesso, LocalDate dataNascita, String username, String password, String email, boolean isAdmin) {

        try {
            Map<String, Object> insertData = getMap(nome, cognome, codiceFiscale, sesso, dataNascita, username,
                    password, email, isAdmin);

            String jsonBody = objectMapper.writeValueAsString(insertData);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/inserimentoUtente"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), RispostaInserimentoUser.class);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new InsertUserAPIException("Errore durante il inserimentoUtente", e);
        }
    }

    private static Map<String, Object> getMap(String nome, String cognome, String codiceFiscale, char sesso,
                                              LocalDate dataNascita, String username, String password, String email, boolean isAdmin) {
        Map<String, Object> insertData = new HashMap<>();
        insertData.put("nome", nome);
        insertData.put("cognome", cognome);
        insertData.put("codiceFiscale", codiceFiscale);
        insertData.put("sesso", String.valueOf(sesso));
        insertData.put("dataNascita", dataNascita.toString());
        insertData.put("username", username);
        insertData.put("password", password);
        insertData.put("mail", email);
        insertData.put("isAdmin", isAdmin);
        return insertData;
    }
}
