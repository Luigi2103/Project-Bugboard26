package it.unina.bugboard.inserimentoissue;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unina.bugboard.dto.RichiestaInserimentoIssue;
import it.unina.bugboard.inserimentoissue.exception.InsertIssueApiException;
import it.unina.bugboard.common.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

public class InsertIssueApiService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SessionManager sessionManager;

    public InsertIssueApiService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public RispostaIssueService inserisciIssue(String titolo, String descrizione, byte[] immagine,
            String tipologia, String stato, String priorita, String istruzioni, String richiesta,
            String titoloDocumento, String descrizioneDocumento, String richiestaFunzionalita,
            LocalDate dataCreazione, int idProgetto, int idSegnalatore) {

        try {
            RichiestaInserimentoIssue insertData = RichiestaInserimentoIssue.builder()
                    .titolo(titolo)
                    .descrizione(descrizione)
                    .immagine(immagine)
                    .tipologia(tipologia)
                    .stato(stato)
                    .priorita(priorita)
                    .istruzioni(istruzioni)
                    .richiesta(richiesta)
                    .titoloDocumento(titoloDocumento)
                    .descrizioneDocumento(descrizioneDocumento)
                    .richiestaFunzionalita(richiestaFunzionalita)
                    .dataCreazione(dataCreazione.toString())
                    .idProgetto(idProgetto)
                    .idSegnalatore(idSegnalatore)
                    .build();

            String jsonBody = objectMapper.writeValueAsString(insertData);
            String token = this.sessionManager.getToken();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/issue/inserimento"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), RispostaIssueService.class);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new InsertIssueApiException("Errore durante inserimento issue", e);
        }
    }
}
