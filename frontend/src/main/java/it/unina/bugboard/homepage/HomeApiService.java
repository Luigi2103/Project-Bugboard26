package it.unina.bugboard.homepage;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unina.bugboard.common.SessionManager;
import it.unina.bugboard.dto.RichiestaRecuperoIssue;
import it.unina.bugboard.dto.RispostaRecuperoIssue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeApiService {

    private static final Logger logger = Logger.getLogger(HomeApiService.class.getName());
    private final SessionManager sessionManager;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HomeApiService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public RispostaRecuperoIssue recuperaIssues(Integer idProgetto, Integer idAssegnatario, Integer page) {
        try {
            RichiestaRecuperoIssue richiesta = new RichiestaRecuperoIssue(idProgetto, idAssegnatario, page, 6); // Default

            String jsonBody = objectMapper.writeValueAsString(richiesta);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/issue/recupera"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + sessionManager.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.log(Level.SEVERE, "Errore durante il recupero delle issues: HTTP {0}", response.statusCode());
                logger.log(Level.SEVERE, "Response body: {0}", response.body());
            }

            return objectMapper.readValue(response.body(), RispostaRecuperoIssue.class);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("Operazione interrotta durante il recupero delle issues per progetto");
            RispostaRecuperoIssue errorResponse = new RispostaRecuperoIssue();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Operazione interrotta");
            return errorResponse;
        } catch (Exception e) {
            logger.info("Errore durante il recupero delle issues per progetto");
            RispostaRecuperoIssue errorResponse = new RispostaRecuperoIssue();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Errore di connessione: " + e.getMessage());
            return errorResponse;
        }
    }

}