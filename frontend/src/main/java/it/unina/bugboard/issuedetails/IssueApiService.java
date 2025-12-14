package it.unina.bugboard.issuedetails;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unina.bugboard.dto.RichiestaInserimentoCommentoIssue;
import it.unina.bugboard.dto.RispostaInserimentoCommentoIssue;
import it.unina.bugboard.common.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class IssueApiService {

    private final SessionManager sessionManager;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    public IssueApiService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public RispostaDettaglioIssue recuperaDettaglio(Integer idIssue) {
        try {
            RichiestaDettaglioIssue richiesta = new RichiestaDettaglioIssue(idIssue);
            String jsonBody = objectMapper.writeValueAsString(richiesta);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/issue/dettaglio"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + sessionManager.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Error fetching issue detail: " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), RispostaDettaglioIssue.class);

        } catch (Exception e) {
            e.printStackTrace();
            RispostaDettaglioIssue errorResponse = new RispostaDettaglioIssue();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Errore di connessione: " + e.getMessage());
            return errorResponse;
        }
    }

    public RispostaInserimentoCommentoIssue inserisciCommento(RichiestaInserimentoCommentoIssue richiesta) {
        try {
            String jsonBody = objectMapper.writeValueAsString(richiesta);

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/issue/commento"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + sessionManager.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

            if (sessionManager.getUserId() != null) {
                builder.header("X-User-Id", String.valueOf(sessionManager.getUserId()));
            }

            HttpRequest request = builder.build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 201 && response.statusCode() != 200) {
                System.err.println("Error inserting comment: " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), RispostaInserimentoCommentoIssue.class);

        } catch (Exception e) {
            e.printStackTrace();
            return new RispostaInserimentoCommentoIssue(false, "Errore di connessione: " + e.getMessage(), null);
        }
    }
}