package it.unina.bugboard.issuedetails;

import it.unina.bugboard.common.SessionManager;
import it.unina.bugboard.dto.IssueDTO;
import it.unina.bugboard.navigation.SceneRouter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class DettaglioIssueController implements Initializable {

    @FXML
    private Label labelTitolo;
    @FXML
    private Label labelStato;
    @FXML
    private Label labelPriorita;
    @FXML
    private Label labelTipologia;
    @FXML
    private Label labelDescrizione;
    @FXML
    private Label labelDataCreazione;
    @FXML
    private VBox containerCampiSpecifici;
    @FXML
    private FlowPane tagsContainer;
    @FXML
    private ImageView imageFoto;
    @FXML
    private Label labelNoAllegati;
    @FXML
    private Button btnIndietro;

    private final IssueApiService apiService;
    private final SessionManager sessionManager;
    private Integer issueId;
    private static final Logger logger = Logger.getLogger(DettaglioIssueController.class.getName());

    public DettaglioIssueController(IssueApiService apiService, SessionManager sessionManager) {
        this.apiService = apiService;
        this.sessionManager = sessionManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // L'issueId sarà passato tramite SceneRouter
    }

    public void setIssueId(Integer issueId) {
        this.issueId = issueId;
        caricaDettagli();
    }

    private void caricaDettagli() {
        if (issueId == null) {
            mostraErrore("ID Issue non valido");
            return;
        }

        RispostaDettaglioIssue risposta = apiService.recuperaDettaglio(issueId);

        if (risposta != null && risposta.isSuccess() && risposta.getIssue() != null) {
            IssueDTO issue = risposta.getIssue();
            popolaCampi(issue);

            if (risposta.getFoto() != null && risposta.getFoto().length > 0) {
                mostraImmagine(risposta.getFoto());
            } else {
                mostraNoAllegati();
            }
        } else {
            mostraErrore(risposta != null ? risposta.getMessage() : "Errore nel caricamento");
        }
    }

    private void popolaCampi(IssueDTO issue) {
        labelTitolo.setText(issue.getTitolo());
        String data = issue.getDataCreazione();
        if (data != null && data.contains("T")) {
            data = data.split("T")[0];
        } else if (data != null && data.length() > 10) {
            data = data.substring(0, 10);
        }
        labelDataCreazione.setText("Aperta il: " + (data != null ? data : "N/D"));

        labelStato.setText("Stato: " + (issue.getStato() != null ? issue.getStato() : ""));
        applicaStileBadge(labelStato, issue.getStato(), "STATO");

        labelPriorita.setText("Priorità: " + (issue.getPriorita() != null ? issue.getPriorita() : "N/D"));
        applicaStileBadge(labelPriorita, issue.getPriorita(), "PRIORITA");

        labelTipologia.setText("Tipologia: " + (issue.getTipologia() != null ? issue.getTipologia() : ""));
        applicaStileBadge(labelTipologia, issue.getTipologia(), "TIPOLOGIA");

        labelDescrizione.setText(issue.getDescrizione());

        popolaCampiSpecificiPerTipologia(issue);
        popolaTags(issue);
    }

    private void popolaCampiSpecificiPerTipologia(IssueDTO issue) {
        containerCampiSpecifici.getChildren().clear();

        switch (issue.getTipologia()) {
            case "BUG":
                aggiungiCampoSpecifico("Passi per riprodurre", issue.getPassiPerRiprodurre());
                break;
            case "QUESTION":
                aggiungiCampoSpecifico("Richiesta", issue.getRichiesta());
                break;
            case "DOCUMENTATION":
                aggiungiCampoSpecifico("Titolo Documento", issue.getTitoloDocumento());
                aggiungiCampoSpecifico("Descrizione Problema", issue.getDescrizioneProblema());
                break;
            case "FEATURE":
                aggiungiCampoSpecifico("Richiesta Funzionalità", issue.getRichiestaFunzionalita());
                break;
            default:
                break;
        }
    }

    private void aggiungiCampoSpecifico(String etichetta, String valore) {
        if (valore != null && !valore.isEmpty()) {
            Label label = new Label(etichetta + ":");
            label.getStyleClass().add("field-label");
            label.getStyleClass().add("section-label");
            Label value = new Label(valore);
            value.setWrapText(true);
            containerCampiSpecifici.getChildren().addAll(label, value);
        }
    }

    private void popolaTags(IssueDTO issue) {
        if (issue.getTags() != null && !issue.getTags().isEmpty()) {
            tagsContainer.getChildren().clear();
            for (String tag : issue.getTags()) {
                Label tagLabel = new Label(tag);
                tagLabel.getStyleClass().add("tag-label");
                tagLabel.setStyle(
                        "-fx-background-color: #EBF5FB; -fx-text-fill: #2980B9; -fx-padding: 3 8; " +
                                "-fx-background-radius: 12; -fx-border-color: #A9CCE3; -fx-border-radius: 12;");
                tagsContainer.getChildren().add(tagLabel);
            }
        }
    }

    private void mostraImmagine(byte[] fotoBytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(fotoBytes);
            Image image = new Image(bis);
            imageFoto.setImage(image);
            imageFoto.setVisible(true);
            imageFoto.setManaged(true);

            if (labelNoAllegati != null) {
                labelNoAllegati.setVisible(false);
                labelNoAllegati.setManaged(false);
            }
        } catch (Exception e) {
            logger.info("Errore durante la immagine della foto: " + e.getMessage());
        }
    }

    private void mostraNoAllegati() {
        if (imageFoto != null) {
            imageFoto.setVisible(false);
            imageFoto.setManaged(false);
        }
        if (labelNoAllegati != null) {
            labelNoAllegati.setVisible(true);
            labelNoAllegati.setManaged(true);
        }
    }

    private void applicaStileBadge(Label label, String valore, String tipo) {
        if (valore == null)
            valore = "";
        valore = valore.toUpperCase();

        String style = "-fx-background-radius: 4; -fx-padding: 2 6; ";

        if ("PRIORITA".equals(tipo)) {
            if (valore.contains("ALTA")) {
                style += "-fx-background-color: #F8D7DA; -fx-text-fill: #721C24;"; // Rosso
            } else if (valore.contains("MEDIA")) {
                style += "-fx-background-color: #FFF3CD; -fx-text-fill: #856404;"; // Giallo
            } else {
                style += "-fx-background-color: #D4EDDA; -fx-text-fill: #155724;"; // Verde (Bassa)
            }
        } else if ("STATO".equals(tipo)) {
            if (valore.contains("APERTA")) {
                style += "-fx-background-color: #D1ECF1; -fx-text-fill: #0C5460;"; // Azzurro
            } else if (valore.contains("CHIUSA")) {
                style += "-fx-background-color: #E2E3E5; -fx-text-fill: #383D41;"; // Grigio
            } else {
                style += "-fx-background-color: #FFF3CD; -fx-text-fill: #856404;"; // Giallo (In Corso)
            }
        } else {

            style += "-fx-background-color: #EBF5FB; -fx-text-fill: #2980B9;"; // Blu chiaro generico
        }

        label.setStyle(style);
    }

    private void mostraErrore(String messaggio) {
        labelTitolo.setText("Errore");
        labelDescrizione.setText(messaggio);
    }

    @FXML
    private void tornaIndietro() {
        SceneRouter.cambiaScena("/it/unina/bugboard/fxml/home.fxml", 1200, 800, "BugBoard - Home");
    }
}