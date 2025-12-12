package it.unina.bugboard.homepage;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unina.bugboard.common.SessionManager;
import it.unina.bugboard.dto.RichiestaRecuperoIssue;
import it.unina.bugboard.dto.RispostaRecuperoIssue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HomeApiService {

    private final SessionManager sessionManager;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HomeApiService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public RispostaRecuperoIssue recuperaIssues(Integer idProgetto, Integer idAssegnatario) {
        try {
            RichiestaRecuperoIssue richiesta = new RichiestaRecuperoIssue(idProgetto, idAssegnatario);
            String jsonBody = objectMapper.writeValueAsString(richiesta);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/issue/recupera"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + sessionManager.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Error fetching issues: " + response.statusCode());
                System.err.println("Response body: " + response.body());
            }

            return objectMapper.readValue(response.body(), RispostaRecuperoIssue.class);

        } catch (Exception e) {
            e.printStackTrace();
            RispostaRecuperoIssue errorResponse = new RispostaRecuperoIssue();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Errore di connessione: " + e.getMessage());
            return errorResponse;
        }
    }
}
