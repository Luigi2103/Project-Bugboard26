package it.unina.bugboard.issuedetails;

import it.unina.bugboard.common.SessionManager;
import it.unina.bugboard.dto.IssueDTO;
import it.unina.bugboard.dto.CommentoDTO;
import it.unina.bugboard.dto.RichiestaInserimentoCommentoIssue;
import it.unina.bugboard.dto.RispostaInserimentoCommentoIssue;
import java.util.List;
import java.util.logging.Logger;
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
    private VBox imageContainer;
    @FXML
    private Label labelNoAllegati;
    @FXML
    private VBox campiCommentiContainer;
    @FXML
    private Label placeholderCommenti;
    @FXML
    private javafx.scene.control.TextArea txtNuovoCommento;
    @FXML
    private Button btnInvia;
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
        if (btnInvia != null && txtNuovoCommento != null) {
            btnInvia.disableProperty().bind(
                    txtNuovoCommento.textProperty().isEmpty());
        }

        if (imageFoto != null && imageContainer != null) {
            imageFoto.fitWidthProperty().bind(imageContainer.widthProperty().subtract(20));
        }
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
            popolaCommenti(risposta.getCommenti());

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

    @FXML
    private javafx.scene.control.ScrollPane scrollPaneCommenti;

    private void popolaCommenti(List<CommentoDTO> commenti) {
        campiCommentiContainer.getChildren().clear();

        if (commenti == null || commenti.isEmpty()) {
            if (placeholderCommenti != null) {
                placeholderCommenti.setVisible(true);
                placeholderCommenti.setManaged(true);
                campiCommentiContainer.getChildren().add(placeholderCommenti);
            }
            return;
        }

        for (it.unina.bugboard.dto.CommentoDTO c : commenti) {
            campiCommentiContainer.getChildren().add(creaBoxCommento(c));
        }

        // Scroll to bottom
        if (scrollPaneCommenti != null) {
            javafx.application.Platform.runLater(() -> scrollPaneCommenti.setVvalue(1.0));
        }
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
            logger.info("Errore carimento immagine : " + e.getMessage());
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
                style += "-fx-background-color: #D4EDDA; -fx-text-fill: #155724;";
            }
        } else if ("STATO".equals(tipo)) {
            if (valore.contains("APERTA")) {
                style += "-fx-background-color: #D1ECF1; -fx-text-fill: #0C5460;";
            } else if (valore.contains("CHIUSA")) {
                style += "-fx-background-color: #E2E3E5; -fx-text-fill: #383D41;";
            } else {
                style += "-fx-background-color: #FFF3CD; -fx-text-fill: #856404;";
            }
        } else {

            style += "-fx-background-color: #EBF5FB; -fx-text-fill: #2980B9;";
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

    @FXML
    private void inviaCommento() {

        if (txtNuovoCommento == null)
            return;
        String testo = txtNuovoCommento.getText();
        if (testo == null || testo.trim().isEmpty()) {
            return;
        }

        RichiestaInserimentoCommentoIssue richiesta = new RichiestaInserimentoCommentoIssue(issueId, testo);
        RispostaInserimentoCommentoIssue risposta = apiService.inserisciCommento(richiesta);

        if (risposta != null && risposta.isSuccess()) {
            if (campiCommentiContainer != null) {
                if (placeholderCommenti != null && placeholderCommenti.isVisible()) {
                    placeholderCommenti.setVisible(false);
                    placeholderCommenti.setManaged(false);
                    campiCommentiContainer.getChildren().remove(placeholderCommenti);
                }

                CommentoDTO nuovoCommentoDTO = risposta.getCommento();
                if (nuovoCommentoDTO == null) {
                    nuovoCommentoDTO = new CommentoDTO();
                    nuovoCommentoDTO.setTesto(testo);
                    nuovoCommentoDTO.setNomeUtente("Tu");
                    nuovoCommentoDTO.setCognomeUtente("");
                    nuovoCommentoDTO.setData(java.time.LocalDate.now());
                } else {
                    if (nuovoCommentoDTO.getNomeUtente() == null) {
                        nuovoCommentoDTO.setNomeUtente("Tu");
                        nuovoCommentoDTO.setCognomeUtente("");
                    }
                }

                VBox commentoBox = creaBoxCommento(nuovoCommentoDTO);
                campiCommentiContainer.getChildren().add(commentoBox);

                // Scroll to bottom
                if (scrollPaneCommenti != null) {
                    javafx.application.Platform.runLater(() -> scrollPaneCommenti.setVvalue(1.0));
                }
            }
            txtNuovoCommento.clear();
        } else {
            mostraErrore(risposta != null ? risposta.getMessage() : "Errore nell'inserimento del commento");
        }
    }

    private VBox creaBoxCommento(it.unina.bugboard.dto.CommentoDTO c) {
        VBox commentoBox = new VBox(5);
        commentoBox.setStyle(
                "-fx-background-color: #F8F9F9; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #E5E8E8; -fx-border-radius: 5;");

        String autore = (c.getNomeUtente() != null ? c.getNomeUtente() : "") + " "
                + (c.getCognomeUtente() != null ? c.getCognomeUtente() : "");
        if (autore.trim().isEmpty())
            autore = "Tu";

        Label lblAutore = new Label(autore);
        lblAutore.setStyle("-fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        Label lblData = new Label(c.getData() != null ? c.getData().toString() : java.time.LocalDate.now().toString());
        lblData.setStyle("-fx-text-fill: #7F8C8D; -fx-font-size: 11px;");

        Label lblTesto = new Label(c.getTesto());
        lblTesto.setWrapText(true);
        lblTesto.setStyle("-fx-text-fill: #34495E;");

        commentoBox.getChildren().addAll(lblAutore, lblData, lblTesto);
        return commentoBox;
    }
}