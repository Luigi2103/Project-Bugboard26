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

    // REFACTORED: Grouped related parameters into data classes
    public static class DatiIssue {
        private final DatiBase base;
        private final DatiTipologia tipologia;
        private final DatiDocumento documento;
        private final DatiContesto contesto;

        public DatiIssue(DatiBase base, DatiTipologia tipologia, DatiDocumento documento, DatiContesto contesto) {
            this.base = base;
            this.tipologia = tipologia;
            this.documento = documento;
            this.contesto = contesto;
        }

        public DatiBase getBase() {
            return base;
        }

        public DatiTipologia getTipologia() {
            return tipologia;
        }

        public DatiDocumento getDocumento() {
            return documento;
        }

        public DatiContesto getContesto() {
            return contesto;
        }
    }

    // Base issue information (title, description, image)
    public static class DatiBase {
        private final String titolo;
        private final String descrizione;
        private final byte[] immagine;

        public DatiBase(String titolo, String descrizione, byte[] immagine) {
            this.titolo = titolo;
            this.descrizione = descrizione;
            this.immagine = immagine;
        }

        public String getTitolo() {
            return titolo;
        }

        public String getDescrizione() {
            return descrizione;
        }

        public byte[] getImmagine() {
            return immagine;
        }
    }

    // Issue type-specific data (tipologia, stato, priorita)
    public static class DatiTipologia {
        private final String tipologia;
        private final String stato;
        private final String priorita;
        private final String istruzioni;
        private final String richiesta;
        private final String richiestaFunzionalita;

        public DatiTipologia(String tipologia, String stato, String priorita,
                             String istruzioni, String richiesta, String richiestaFunzionalita) {
            this.tipologia = tipologia;
            this.stato = stato;
            this.priorita = priorita;
            this.istruzioni = istruzioni;
            this.richiesta = richiesta;
            this.richiestaFunzionalita = richiestaFunzionalita;
        }

        public String getTipologia() {
            return tipologia;
        }

        public String getStato() {
            return stato;
        }

        public String getPriorita() {
            return priorita;
        }

        public String getIstruzioni() {
            return istruzioni;
        }

        public String getRichiesta() {
            return richiesta;
        }

        public String getRichiestaFunzionalita() {
            return richiestaFunzionalita;
        }
    }

    // Document-related data
    public static class DatiDocumento {
        private final String titoloDocumento;
        private final String descrizioneDocumento;

        public DatiDocumento(String titoloDocumento, String descrizioneDocumento) {
            this.titoloDocumento = titoloDocumento;
            this.descrizioneDocumento = descrizioneDocumento;
        }

        public String getTitoloDocumento() {
            return titoloDocumento;
        }

        public String getDescrizioneDocumento() {
            return descrizioneDocumento;
        }
    }

    // Context data (date, project, reporter)
    public static class DatiContesto {
        private final LocalDate dataCreazione;
        private final int idProgetto;
        private final int idSegnalatore;

        public DatiContesto(LocalDate dataCreazione, int idProgetto, int idSegnalatore) {
            this.dataCreazione = dataCreazione;
            this.idProgetto = idProgetto;
            this.idSegnalatore = idSegnalatore;
        }

        public LocalDate getDataCreazione() {
            return dataCreazione;
        }

        public int getIdProgetto() {
            return idProgetto;
        }

        public int getIdSegnalatore() {
            return idSegnalatore;
        }
    }

    public RispostaIssueService inserisciIssue(DatiIssue datiIssue) {
        try {
            RichiestaInserimentoIssue insertData = RichiestaInserimentoIssue.builder()
                    .titolo(datiIssue.getBase().getTitolo())
                    .descrizione(datiIssue.getBase().getDescrizione())
                    .immagine(datiIssue.getBase().getImmagine())
                    .tipologia(datiIssue.getTipologia().getTipologia())
                    .stato(datiIssue.getTipologia().getStato())
                    .priorita(datiIssue.getTipologia().getPriorita())
                    .istruzioni(datiIssue.getTipologia().getIstruzioni())
                    .richiesta(datiIssue.getTipologia().getRichiesta())
                    .titoloDocumento(datiIssue.getDocumento().getTitoloDocumento())
                    .descrizioneDocumento(datiIssue.getDocumento().getDescrizioneDocumento())
                    .richiestaFunzionalita(datiIssue.getTipologia().getRichiestaFunzionalita())
                    .dataCreazione(datiIssue.getContesto().getDataCreazione().toString())
                    .idProgetto(datiIssue.getContesto().getIdProgetto())
                    .idSegnalatore(datiIssue.getContesto().getIdSegnalatore())
                    .build();

            String jsonBody = objectMapper.writeValueAsString(insertData);
            String token = this.sessionManager.getToken();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/issues"))
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