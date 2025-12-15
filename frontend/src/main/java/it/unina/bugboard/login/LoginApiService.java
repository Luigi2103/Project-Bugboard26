package it.unina.bugboard.login;

import java.net.URI;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unina.bugboard.login.exception.LoginApiException;

public class LoginApiService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RispostaLogin login(String username, String password) {
        try {
            // Modalita is ignored by backend logic now, but preserved in DTO for
            // compatibility
            String modalita = "utente";

            Map<String, String> loginData = new HashMap<>();
            loginData.put("username", username);
            loginData.put("password", password);
            loginData.put("modalita", modalita);

            String jsonBody = objectMapper.writeValueAsString(loginData);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(response.body(), RispostaLogin.class);

        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new LoginApiException("Errore durante il login", e);
        }
    }

}
