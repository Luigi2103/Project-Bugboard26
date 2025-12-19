package it.unina.bugboard.recovery;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unina.bugboard.recovery.exception.ErroreServizioAPIexeception;

public class RecoveryApiService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RecoveryRespond updateApi(String username, String password) throws ErroreServizioAPIexeception {

        try {
            Map<String, String> updateData = new HashMap<>();
            updateData.put("username", username);
            updateData.put("password", password);

            String json = objectMapper.writeValueAsString(updateData);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/auth/update"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(response.body(), RecoveryRespond.class);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ErroreServizioAPIexeception("Errore durante la chiamata al servizio Recovery API");
        }
    }

}
