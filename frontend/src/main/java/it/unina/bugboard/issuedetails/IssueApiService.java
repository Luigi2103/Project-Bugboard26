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


    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_USER_ID = "X-User-Id";


    private static final String CONTENT_TYPE_JSON = "application/json";


    private static final String BEARER_PREFIX = "Bearer ";

    // Error Messages
    private static final String MSG_OPERAZIONE_INTERROTTA = "Operazione interrotta";
    private static final String MSG_ERRORE_CONNESSIONE = "Errore di connessione: ";


    private static final String BASE_URL = "http://72.146.234.83:8080/api/issues/";

    private final SessionManager sessionManager;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    public IssueApiService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public RispostaDettaglioIssue recuperaDettaglio(Integer idIssue) {
        try {
            String url = BASE_URL + idIssue;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_AUTHORIZATION, BEARER_PREFIX + sessionManager.getToken())
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
            return createErrorResponse(MSG_OPERAZIONE_INTERROTTA);
        } catch (Exception e) {
            logger.info("Errore durante recupero dettaglio issue: " + e.getMessage());
            return createErrorResponse(MSG_ERRORE_CONNESSIONE + e.getMessage());
        }
    }

    public RispostaInserimentoCommentoIssue inserisciCommento(RichiestaInserimentoCommentoIssue richiesta) {
        try {
            Integer idIssue = richiesta.getIdIssue();
            String jsonBody = objectMapper.writeValueAsString(richiesta);

            String url = BASE_URL + idIssue + "/comments";

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_AUTHORIZATION, BEARER_PREFIX + sessionManager.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

            if (sessionManager.getUserId() != null)
                builder.header(HEADER_USER_ID, String.valueOf(sessionManager.getUserId()));

            HttpRequest request = builder.build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 201 && response.statusCode() != 200)
                logger.info("Errore durante l'inserimento del commento");

            return objectMapper.readValue(response.body(), RispostaInserimentoCommentoIssue.class);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("Operazione interrotta durante l'inserimento del commento per issue: " + e.getMessage());
            return new RispostaInserimentoCommentoIssue(false, MSG_OPERAZIONE_INTERROTTA, null);
        } catch (Exception e) {
            logger.info("Errore durante l'inserimento del commento: " + e.getMessage());
            return new RispostaInserimentoCommentoIssue(false, MSG_ERRORE_CONNESSIONE + e.getMessage(), null);
        }
    }

    public RispostaDettaglioIssue modificaIssue(Integer idIssue, String stato, String priorita) {
        try {
            String jsonBody = String.format("{\"stato\":\"%s\", \"priorita\":\"%s\"}", stato, priorita);

            String url = BASE_URL + idIssue;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_AUTHORIZATION, BEARER_PREFIX + sessionManager.getToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
                logger.info("Errore durante la modifica dell'issue");

            return objectMapper.readValue(response.body(), RispostaDettaglioIssue.class);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("Operazione interrotta durante la modifica dell'issue: " + e.getMessage());
            return createErrorResponse(MSG_OPERAZIONE_INTERROTTA);
        } catch (Exception e) {
            logger.info("Errore durante la modifica dell'issue: " + e.getMessage());
            return createErrorResponse(MSG_ERRORE_CONNESSIONE + e.getMessage());
        }
    }

    private RispostaDettaglioIssue createErrorResponse(String message) {
        RispostaDettaglioIssue errorResponse = new RispostaDettaglioIssue();
        errorResponse.setSuccess(false);
        errorResponse.setMessage(message);
        return errorResponse;
    }
}