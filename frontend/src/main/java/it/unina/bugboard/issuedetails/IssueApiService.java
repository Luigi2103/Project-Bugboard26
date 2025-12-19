package it.unina.bugboard.issuedetails;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unina.bugboard.dto.RichiestaInserimentoCommentoIssue;
import it.unina.bugboard.dto.RispostaInserimentoCommentoIssue;
import it.unina.bugboard.common.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IssueApiService {

    private static final Logger logger = Logger.getLogger(IssueApiService.class.getName());
    private final SessionManager sessionManager;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    public IssueApiService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public RispostaDettaglioIssue recuperaDettaglio(Integer idIssue) {
        try {
            String url = "http://localhost:8080/api/issues/" + idIssue;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + sessionManager.getToken())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.log(Level.SEVERE, "Errore durante il recupero del dettaglio issue: HTTP {0}",
                        response.statusCode());
            }

            return objectMapper.readValue(response.body(), RispostaDettaglioIssue.class);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("Operazione interrotta durante il recupero del dettaglio issue: " + e.getMessage());
            RispostaDettaglioIssue errorResponse = new RispostaDettaglioIssue();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Operazione interrotta");
            return errorResponse;
        } catch (Exception e) {
            logger.info("Errore durente recupero dettaglio issue: " + e.getMessage());
            RispostaDettaglioIssue errorResponse = new RispostaDettaglioIssue();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Errore di connessione: " + e.getMessage());
            return errorResponse;
        }
    }

    public RispostaInserimentoCommentoIssue inserisciCommento(RichiestaInserimentoCommentoIssue richiesta) {
        try {
            Integer idIssue = richiesta.getIdIssue();
            String jsonBody = objectMapper.writeValueAsString(richiesta);

            String url = "http://localhost:8080/api/issues/" + idIssue + "/comments";

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + sessionManager.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

            if (sessionManager.getUserId() != null) {
                builder.header("X-User-Id", String.valueOf(sessionManager.getUserId()));
            }

            HttpRequest request = builder.build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 201 && response.statusCode() != 200) {
                logger.info("Errore durante l'inserimento del commento");
            }

            return objectMapper.readValue(response.body(), RispostaInserimentoCommentoIssue.class);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("Operazione interrotta durante l'inserimento del commento per issue: " + e.getMessage());
            return new RispostaInserimentoCommentoIssue(false, "Operazione interrotta", null);
        } catch (Exception e) {
            logger.info("Errore durante l'inserimento del commento: " + e.getMessage());
            return new RispostaInserimentoCommentoIssue(false, "Errore di connessione: " + e.getMessage(), null);
        }
    }

    public RispostaDettaglioIssue modificaIssue(Integer idIssue, String stato, String priorita) {
        try {
            String jsonBody = String.format("{\"stato\":\"%s\", \"priorita\":\"%s\"}", stato, priorita);

            String url = "http://localhost:8080/api/issues/" + idIssue;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + sessionManager.getToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.info("Errore durante la modifica dell'issue");
            }

            return objectMapper.readValue(response.body(), RispostaDettaglioIssue.class);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("Operazione interrotta durante la modifica dell'issue: " + e.getMessage());
            RispostaDettaglioIssue err = new RispostaDettaglioIssue();
            err.setSuccess(false);
            err.setMessage("Operazione interrotta");
            return err;
        } catch (Exception e) {
            logger.info("Errore durante la modifica dell'issue: " + e.getMessage());
            RispostaDettaglioIssue err = new RispostaDettaglioIssue();
            err.setSuccess(false);
            err.setMessage("Errore di connessione: " + e.getMessage());
            return err;
        }
    }
}