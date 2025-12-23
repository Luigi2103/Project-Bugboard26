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

    private static final String TIPO_STATO = "STATO";
    private static final String TIPO_PRIORITA = "PRIORITA";
    private static final String TIPO_TIPOLOGIA = "TIPOLOGIA";

    private static final Logger LOGGER = Logger.getLogger(DettaglioIssueController.class.getName());

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
    @FXML
    private Button btnModifica;
    @FXML
    private Button btnSalva;
    @FXML
    private Button btnAnnulla;
    @FXML
    private javafx.scene.control.ComboBox<String> cmbStato;
    @FXML
    private javafx.scene.control.ComboBox<String> cmbPriorita;
    @FXML
    private javafx.scene.control.ScrollPane scrollPaneCommenti;
    @FXML
    private javafx.scene.control.ScrollPane scrollPaneCronologia;
    @FXML
    private VBox cronologiaContainer;
    @FXML
    private Label placeholderCronologia;

    private final IssueApiService apiService;
    private final SessionManager sessionManager;
    private Integer issueId;

    public DettaglioIssueController(IssueApiService apiService, SessionManager sessionManager) {
        this.apiService = apiService;
        this.sessionManager = sessionManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (btnInvia != null && txtNuovoCommento != null) {
            btnInvia.disableProperty().bind(txtNuovoCommento.textProperty().isEmpty());
        }

        inizializzaComboBoxes();

        if (imageFoto != null) {
            imageFoto.setOnMouseEntered(e -> imageFoto.setCursor(javafx.scene.Cursor.HAND));
            imageFoto.setOnMouseExited(e -> imageFoto.setCursor(javafx.scene.Cursor.DEFAULT));
            imageFoto.setOnMouseClicked(e -> {
                if (imageFoto.getImage() != null) {
                    apriImmagineIngrandita(imageFoto.getImage());
                }
            });
        }
    }

    private void inizializzaComboBoxes() {
        if (cmbStato != null) {
            cmbStato.getItems().addAll("TO-DO", "IN PROGRESS", "RESOLVED", "CLOSED");
            cmbStato.valueProperty().addListener((obs, oldVal, newVal) -> checkChanges());
        }
        if (cmbPriorita != null) {
            cmbPriorita.getItems().addAll("BASSA", "MEDIA", "ALTA", "MASSIMA");
            cmbPriorita.valueProperty().addListener((obs, oldVal, newVal) -> checkChanges());
        }
    }

    public void setModificaMode(boolean attiva) {
        if (attiva)
            abilitaModifica();
    }

    private String initialStato;
    private String initialPriorita;

    @FXML
    private void abilitaModifica() {

        this.initialStato = labelStato.getText();
        this.initialPriorita = labelPriorita.getText();

        toggleBottoniModifica(true);
        toggleCampiModifica(true);

        if (cmbStato != null) {
            cmbStato.setValue(this.initialStato);
        }
        if (cmbPriorita != null) {
            cmbPriorita.setValue(this.initialPriorita);
        }

        checkChanges();
    }

    private void checkChanges() {
        if (btnSalva == null || cmbStato == null || cmbPriorita == null)
            return;

        String currentStato = cmbStato.getValue();
        String currentPriorita = cmbPriorita.getValue();

        boolean changed = isDifferent(currentStato, initialStato) || isDifferent(currentPriorita, initialPriorita);
        btnSalva.setDisable(!changed);
    }

    private boolean isDifferent(String s1, String s2) {
        return s1 != null ? !s1.equals(s2) : s2 != null;
    }

    @FXML
    private void annullaModifiche() {
        ripristinaVista();
    }

    @FXML
    private void salvaModifiche() {
        if (cmbStato == null || cmbPriorita == null)
            return;

        String nuovoStato = cmbStato.getValue();
        String nuovaPriorita = cmbPriorita.getValue();

        RispostaDettaglioIssue risposta = apiService.modificaIssue(issueId, nuovoStato, nuovaPriorita);

        if (risposta != null && risposta.isSuccess()) {
            aggiornaIssueDopoModifica(risposta, nuovoStato, nuovaPriorita);
            ripristinaVista();
            // Ricarica la pagina per aggiornare la cronologia
            caricaDettagli();
        } else {
            mostraErrore(risposta != null ? risposta.getMessage() : "Errore durante il salvataggio");
        }
    }

    private void aggiornaIssueDopoModifica(RispostaDettaglioIssue risposta, String nuovoStato, String nuovaPriorita) {
        if (risposta.getIssue() != null) {
            popolaCampi(risposta.getIssue());
        } else {
            aggiornaLabelsLocalmente(nuovoStato, nuovaPriorita);
        }
    }

    private void aggiornaLabelsLocalmente(String nuovoStato, String nuovaPriorita) {
        labelStato.setText(nuovoStato);
        applicaStileBadge(labelStato, nuovoStato, TIPO_STATO);
        labelPriorita.setText(nuovaPriorita);
        applicaStileBadge(labelPriorita, nuovaPriorita, TIPO_PRIORITA);
    }

    private void ripristinaVista() {
        toggleBottoniModifica(false);
        toggleCampiModifica(false);
    }

    private void toggleBottoniModifica(boolean modalitaModifica) {
        if (btnModifica != null) {
            btnModifica.setVisible(!modalitaModifica);
            btnModifica.setManaged(!modalitaModifica);
        }
        if (btnSalva != null) {
            btnSalva.setVisible(modalitaModifica);
            btnSalva.setManaged(modalitaModifica);
        }
        if (btnAnnulla != null) {
            btnAnnulla.setVisible(modalitaModifica);
            btnAnnulla.setManaged(modalitaModifica);
        }
    }

    private void toggleCampiModifica(boolean modalitaModifica) {
        toggleCampoModifica(labelStato, cmbStato, modalitaModifica);
        toggleCampoModifica(labelPriorita, cmbPriorita, modalitaModifica);
    }

    private void toggleCampoModifica(Label label, javafx.scene.control.ComboBox<String> combo,
            boolean modalitaModifica) {
        if (label != null && combo != null) {
            if (modalitaModifica) {
                combo.setValue(label.getText());
            }
            label.setVisible(!modalitaModifica);
            label.setManaged(!modalitaModifica);
            combo.setVisible(modalitaModifica);
            combo.setManaged(modalitaModifica);
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
            processaRispostaDettaglio(risposta);
        } else {
            mostraErrore(risposta != null ? risposta.getMessage() : "Errore nel caricamento");
        }
    }

    private void processaRispostaDettaglio(RispostaDettaglioIssue risposta) {
        IssueDTO issue = risposta.getIssue();
        popolaCampi(issue);
        popolaCommenti(risposta.getCommenti());
        popolaCronologia(risposta.getCronologia());

        if (risposta.getFoto() != null && risposta.getFoto().length > 0) {
            mostraImmagine(risposta.getFoto());
        } else {
            mostraNoAllegati();
        }
    }

    private void popolaCampi(IssueDTO issue) {
        labelTitolo.setText(issue.getTitolo());

        String dataFormattata = formattaData(issue.getDataCreazione());
        labelDataCreazione.setText("Aperta il: " + dataFormattata);

        labelStato.setText(issue.getStato() != null ? issue.getStato() : "");
        applicaStileBadge(labelStato, issue.getStato(), TIPO_STATO);

        labelPriorita.setText(issue.getPriorita() != null ? issue.getPriorita() : "N/D");
        applicaStileBadge(labelPriorita, issue.getPriorita(), TIPO_PRIORITA);

        labelTipologia.setText(issue.getTipologia() != null ? issue.getTipologia() : "");
        applicaStileBadge(labelTipologia, issue.getTipologia(), TIPO_TIPOLOGIA);

        labelDescrizione.setText(issue.getDescrizione());

        popolaCampiSpecificiPerTipologia(issue);
        popolaTags(issue);
    }

    private String formattaData(String data) {
        if (data == null)
            return "N/D";
        if (data.contains("T")) {
            return data.split("T")[0];
        } else if (data.length() > 10) {
            return data.substring(0, 10);
        }
        return data;
    }

    private void popolaCommenti(List<CommentoDTO> commenti) {
        campiCommentiContainer.getChildren().clear();

        if (commenti == null || commenti.isEmpty()) {
            mostraPlaceholderCommenti();
        } else {
            for (CommentoDTO c : commenti) {
                campiCommentiContainer.getChildren().add(creaBoxCommento(c));
            }
        }
        scrollToBottomCommenti();
    }

    private void popolaCronologia(List<it.unina.bugboard.dto.CronologiaDTO> cronologia) {
        if (cronologiaContainer == null)
            return;
        cronologiaContainer.getChildren().clear();

        if (cronologia == null || cronologia.isEmpty()) {
            mostraPlaceholderCronologia();
        } else {
            for (it.unina.bugboard.dto.CronologiaDTO c : cronologia) {
                cronologiaContainer.getChildren().add(creaBoxCronologia(c));
            }
        }
        scrollToBottomCronologia();
    }

    private void mostraPlaceholderCommenti() {
        if (placeholderCommenti != null) {
            placeholderCommenti.setVisible(true);
            placeholderCommenti.setManaged(true);
            campiCommentiContainer.getChildren().add(placeholderCommenti);
        }
    }

    private void mostraPlaceholderCronologia() {
        if (placeholderCronologia != null) {
            placeholderCronologia.setVisible(true);
            placeholderCronologia.setManaged(true);
            cronologiaContainer.getChildren().add(placeholderCronologia);
        }
    }

    private void scrollToBottomCommenti() {
        if (scrollPaneCommenti != null) {
            javafx.application.Platform.runLater(() -> scrollPaneCommenti.setVvalue(1.0));
        }
    }

    private void scrollToBottomCronologia() {
        if (scrollPaneCronologia != null) {
            javafx.application.Platform.runLater(() -> scrollPaneCronologia.setVvalue(1.0));
        }
    }

    private VBox creaBoxCronologia(it.unina.bugboard.dto.CronologiaDTO c) {
        VBox cronologiaBox = new VBox(5);
        cronologiaBox.setStyle(
                "-fx-background-color: #F8F9F9; -fx-padding: 8; -fx-background-radius: 4; -fx-border-color: #E5E8E8; -fx-border-radius: 4; -fx-border-width: 0 0 0 4; -fx-border-color: #AAB7B8;");

        String dataStr = c.getData() != null ? formattaData(c.getData().toString()) : "N/D";
        Label lblData = new Label(dataStr);
        lblData.setStyle("-fx-text-fill: #7F8C8D; -fx-font-size: 10px;");

        Label lblDescrizione = new Label(c.getDescrizione());
        lblDescrizione.setWrapText(true);
        lblDescrizione.setStyle("-fx-text-fill: #34495E; -fx-font-size: 12px;");

        cronologiaBox.getChildren().addAll(lblData, lblDescrizione);
        return cronologiaBox;
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
                Label tagLabel = creaTagLabel(tag);
                tagsContainer.getChildren().add(tagLabel);
            }
        }
    }

    private Label creaTagLabel(String tag) {
        Label tagLabel = new Label(tag);
        tagLabel.getStyleClass().add("tag-label");
        tagLabel.setStyle(
                "-fx-background-color: #EBF5FB; -fx-text-fill: #2980B9; -fx-padding: 3 8; " +
                        "-fx-background-radius: 12; -fx-border-color: #A9CCE3; -fx-border-radius: 12;");
        return tagLabel;
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
            LOGGER.info("Errore caricamento immagine: " + e.getMessage());
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
        String valoreUpper = valore != null ? valore.toUpperCase() : "";
        String style = "-fx-background-radius: 4; -fx-padding: 2 6; ";

        if (TIPO_PRIORITA.equals(tipo)) {
            style += getStylePriorita(valoreUpper);
        } else if (TIPO_STATO.equals(tipo)) {
            style += getStyleStato(valoreUpper);
        } else {
            style += "-fx-background-color: #EBF5FB; -fx-text-fill: #2980B9;";
        }

        label.setStyle(style);
    }

    private String getStylePriorita(String valore) {
        if (valore.contains("ALTA") || valore.contains("MASSIMA")) {
            return "-fx-background-color: #F8D7DA; -fx-text-fill: #721C24;";
        } else if (valore.contains("MEDIA")) {
            return "-fx-background-color: #FFF3CD; -fx-text-fill: #856404;";
        }
        return "-fx-background-color: #D4EDDA; -fx-text-fill: #155724;";
    }

    private String getStyleStato(String valore) {
        if (valore.contains("TO-DO") || valore.contains("APERTA")) {
            return "-fx-background-color: #D1ECF1; -fx-text-fill: #0C5460;";
        } else if (valore.contains("IN PROGRESS") || valore.contains("CORSO")) {
            return "-fx-background-color: #FFF3CD; -fx-text-fill: #856404;";
        } else if (valore.contains("RESOLVED") || valore.contains("CLOSED") || valore.contains("CHIUSA")) {
            return "-fx-background-color: #E2E3E5; -fx-text-fill: #383D41;";
        }
        return "-fx-background-color: #EBF5FB; -fx-text-fill: #2980B9;";
    }

    private void mostraErrore(String messaggio) {
        labelTitolo.setText("Errore");
        labelDescrizione.setText(messaggio);
    }

    @FXML
    private void tornaIndietro() {
        SceneRouter.tornaIndietro();
    }

    @FXML
    private void inviaCommento() {
        if (txtNuovoCommento == null)
            return;

        String testo = txtNuovoCommento.getText();
        if (testo == null || testo.trim().isEmpty())
            return;

        RichiestaInserimentoCommentoIssue richiesta = new RichiestaInserimentoCommentoIssue(issueId, testo);
        RispostaInserimentoCommentoIssue risposta = apiService.inserisciCommento(richiesta);

        if (risposta != null && risposta.isSuccess()) {
            gestisciSuccessoCommento(risposta, testo);
            txtNuovoCommento.clear();
        } else {
            mostraErrore(risposta != null ? risposta.getMessage() : "Errore nell'inserimento del commento");
        }
    }

    private void gestisciSuccessoCommento(RispostaInserimentoCommentoIssue risposta, String testo) {
        if (campiCommentiContainer == null)
            return;

        nascondiPlaceholderCommenti();

        CommentoDTO nuovoCommentoDTO = creaCommentoDaRisposta(risposta, testo);
        VBox commentoBox = creaBoxCommento(nuovoCommentoDTO);
        campiCommentiContainer.getChildren().add(commentoBox);

        scrollToBottomCommenti();
    }

    private void nascondiPlaceholderCommenti() {
        if (placeholderCommenti != null && placeholderCommenti.isVisible()) {
            placeholderCommenti.setVisible(false);
            placeholderCommenti.setManaged(false);
            campiCommentiContainer.getChildren().remove(placeholderCommenti);
        }
    }

    private CommentoDTO creaCommentoDaRisposta(RispostaInserimentoCommentoIssue risposta, String testo) {
        CommentoDTO commentoDTO = risposta.getCommento();

        if (commentoDTO == null) {
            return creaCommentoVuoto(testo);
        }

        if (commentoDTO.getNomeUtente() == null) {
            completaInfoUtente(commentoDTO);
        }

        return commentoDTO;
    }

    private CommentoDTO creaCommentoVuoto(String testo) {
        CommentoDTO commentoDTO = new CommentoDTO();
        commentoDTO.setTesto(testo);
        completaInfoUtente(commentoDTO);
        commentoDTO.setData(java.time.LocalDate.now());
        return commentoDTO;
    }

    private void completaInfoUtente(CommentoDTO commentoDTO) {
        String nome = sessionManager.getNome() != null ? sessionManager.getNome() : sessionManager.getUsername();
        String cognome = sessionManager.getCognome() != null ? sessionManager.getCognome() : "";
        commentoDTO.setNomeUtente(nome);
        commentoDTO.setCognomeUtente(cognome);
    }

    private VBox creaBoxCommento(CommentoDTO c) {
        VBox commentoBox = new VBox(5);
        commentoBox.setStyle(
                "-fx-background-color: #F8F9F9; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #E5E8E8; -fx-border-radius: 5;");

        String autore = determineAutore(c);

        Label lblAutore = new Label(autore);
        lblAutore.setStyle("-fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        String dataStr = c.getData() != null ? c.getData().toString() : java.time.LocalDate.now().toString();
        Label lblData = new Label(dataStr);
        lblData.setStyle("-fx-text-fill: #7F8C8D; -fx-font-size: 11px;");

        Label lblTesto = new Label(c.getTesto());
        lblTesto.setWrapText(true);
        lblTesto.setStyle("-fx-text-fill: #34495E;");

        commentoBox.getChildren().addAll(lblAutore, lblData, lblTesto);
        return commentoBox;
    }

    private String determineAutore(CommentoDTO c) {
        String autore = (c.getNomeUtente() != null ? c.getNomeUtente() : "") + " "
                + (c.getCognomeUtente() != null ? c.getCognomeUtente() : "");
        return autore.trim().isEmpty() ? "Tu" : autore;
    }

    private void apriImmagineIngrandita(Image image) {
        if (image == null)
            return;

        javafx.stage.Stage stage = new javafx.stage.Stage();
        stage.setTitle("Visualizzazione Immagine");

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);

        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(imageView);
        root.setStyle("-fx-background-color: rgba(0,0,0,0.9);");

        imageView.fitWidthProperty().bind(root.widthProperty());
        imageView.fitHeightProperty().bind(root.heightProperty());

        javafx.scene.Scene scene = new javafx.scene.Scene(root, 1000, 800);
        stage.setScene(scene);
        stage.show();
    }
}