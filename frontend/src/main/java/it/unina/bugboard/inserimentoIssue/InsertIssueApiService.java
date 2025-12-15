package it.unina.bugboard.inserimentoIssue;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unina.bugboard.inserimentoIssue.InsertIssueApiService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class InsertIssueApiService
{
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final it.unina.bugboard.common.SessionManager sessionManager;


    public InsertIssueApiService(it.unina.bugboard.common.SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public RispostaIssueService inserisciIssue(String titolo, String descrizione, byte[] immagine,
                                               String tipologia, String stato, String priorita, String istruzioni, String richiesta,
                                               String titoloDocumento, String descrizioneDocumento, String richiestaFunzionalita,
    LocalDate dataCreazione) {

        try {
            Map<String, Object> insertData = getMap(titolo, descrizione, immagine, tipologia, tipologia,
                   stato, priorita,istruzioni, richiesta,titoloDocumento,descrizioneDocumento,richiestaFunzionalita);

            String jsonBody = objectMapper.writeValueAsString(insertData);
            String token = this.sessionManager.getToken();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/inserimentoUtente"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), RispostaInserimentoUser.class);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new InsertUserAPIException("Errore durante inserimento utente", e);
        }
    }


    private static Map<String, Object> getMap(String titolo, String descrizione, byte[] immagine,
                                              String tipologia, String stato, String priorita, String istruzioni, String richiesta,
                                              String titoloDocumento, String descrizioneDocumento, String richiestaFunzionalita,
                                              LocalDate dataCreazione) {
        Map<String, Object> insertData = new HashMap<>();
        insertData.put("titolo", titolo);
        insertData.put("descrizione", descrizione);
        insertData.put("immagine", immagine);
        insertData.put("tipologia", tipologia);
        insertData.put("stato",stato);
        insertData.put("priorita", priorita);
        insertData.put("istruzioni", istruzioni);
        insertData.put("richiesta", richiesta);
        insertData.put("titoloDocumento", titoloDocumento);
        insertData.put("descrizioneDocumento", descrizioneDocumento);
        insertData.put("richiestaFunzionalita", richiestaFunzionalita);
        insertData.put("dataCreazione", dataCreazione.toString());



        return insertData;
    }
}

