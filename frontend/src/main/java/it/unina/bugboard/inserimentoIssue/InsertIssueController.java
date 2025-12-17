package it.unina.bugboard.inserimentoIssue;

import it.unina.bugboard.navigation.SceneRouter;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;

import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class InsertIssueController {

    private static final String ISSUE_BUG = "BUG";
    private static final String ISSUE_QUESTION = "QUESTION";
    private static final String ISSUE_DOC = "DOCUMENTATION";
    private static final String ISSUE_FEATURE = "FEATURE";
    private static final String MSG_INPUT_ERROR = "input-error";

    @FXML
    private TextField campoTitolo;
    @FXML
    private TextArea areaDescrizione;
    @FXML
    private ComboBox<String> comboTipo;
    @FXML
    private ComboBox<String> comboPriorita;

    @FXML
    private VBox containerCampiDinamici;
    @FXML
    private VBox areaUploadImmagine;
    @FXML
    private Label labelNomeFile;
    @FXML
    private Label erroreTitolo;
    @FXML
    private Label erroreDescrizione;
    @FXML
    private Label erroreTipo;
    @FXML
    private Label errorePriorita;

    private File fileSelezionato;

    private final InsertIssueApiService apiService;
    private final it.unina.bugboard.common.SessionManager sessionManager;

    public InsertIssueController(InsertIssueApiService apiService,
            it.unina.bugboard.common.SessionManager sessionManager) {
        this.apiService = apiService;
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        inizializzaComboTipo();
        inizializzaComboPriorita();
        inizializzaListeners();
        inizializzaUpload();
    }

    private void inizializzaComboPriorita() {
        comboPriorita.getItems().addAll("BASSA", "MEDIA", "ALTA", "MASSIMA");
        comboPriorita.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            resetErrorStyles();
        });
    }

    /* -------------------------- INIT -------------------------- */

    private void inizializzaComboTipo() {
        comboTipo.getItems().addAll(ISSUE_BUG, ISSUE_QUESTION, ISSUE_DOC, ISSUE_FEATURE);

        comboTipo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            aggiornaCampiDinamici(newVal);
            resetErrorStyles();
        });
    }

    private void inizializzaListeners() {
        campoTitolo.textProperty().addListener((obs, old, neu) -> resetErrorStyles());
        areaDescrizione.textProperty().addListener((obs, old, neu) -> resetErrorStyles());
    }

    private void inizializzaUpload() {
        areaUploadImmagine.setOnMouseClicked(event -> selezionaImmagine());
    }

    /* -------------------------- CAMPi DINAMICI -------------------------- */

    private void aggiornaCampiDinamici(String tipo) {
        containerCampiDinamici.getChildren().clear();
        if (tipo == null) {
            return;
        }

        switch (tipo) {
            case ISSUE_BUG -> aggiungiCampoTesto("Istruzioni per riprodurre", "Inserisci i passaggi...");
            case ISSUE_QUESTION -> aggiungiCampoTesto("Richiesta specifica", "Qual è la tua domanda?");
            case ISSUE_DOC -> {
                aggiungiCampoTesto("Titolo Documento", "Titolo del documento...");
                aggiungiCampoTesto("Descrizione Problema", "Descrivi il problema...");
            }
            case ISSUE_FEATURE -> aggiungiCampoTesto("Richiesta Funzionalità", "Descrivi la funzionalità...");
            default -> {
                // Campo di default intenzionalmente vuoto
            }
        }
    }

    private void aggiungiCampoTesto(String etichetta, String placeholder) {
        Label label = new Label(etichetta);
        label.getStyleClass().add("field-label");

        TextField textField = new TextField();
        textField.setPromptText(placeholder);
        textField.setMaxWidth(Double.MAX_VALUE);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);

        textField.textProperty().addListener((obs, old, neu) -> {
            textField.getStyleClass().remove(MSG_INPUT_ERROR);
            errorLabel.setManaged(false);
            errorLabel.setVisible(false);
        });

        VBox box = new VBox(5, label, textField, errorLabel);
        containerCampiDinamici.getChildren().add(box);
    }

    /* -------------------------- UPLOAD IMMAGINE -------------------------- */

    private void selezionaImmagine() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona Immagine");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        Stage stage = (Stage) areaUploadImmagine.getScene().getWindow();
        fileSelezionato = fileChooser.showOpenDialog(stage);

        if (fileSelezionato != null) {
            labelNomeFile.setText(fileSelezionato.getName());
        }
    }

    /* -------------------------- NAVIGAZIONE -------------------------- */

    @FXML
    private void tornaIndietro() {
        SceneRouter.cambiaScena("/it/unina/bugboard/fxml/home.fxml", 1200, 800, "BugBoard - Home");
    }

    /* -------------------------- RESET -------------------------- */

    @FXML
    private void pulisciCampi() {
        campoTitolo.clear();
        areaDescrizione.clear();
        comboTipo.getSelectionModel().clearSelection();
        comboPriorita.getSelectionModel().clearSelection();
        containerCampiDinamici.getChildren().clear();

        labelNomeFile.setText("Clicca per caricare un'immagine");
        fileSelezionato = null;

        resetErrorStyles();
    }

    /* -------------------------- VALIDAZIONE -------------------------- */

    @FXML
    private void gestisciInserimento() {
        resetErrorStyles();

        boolean valido = true;

        if (isEmpty(campoTitolo.getText())) {
            mostraErrore(campoTitolo, erroreTitolo, "Il titolo è obbligatorio");
            valido = false;
        }

        if (isEmpty(areaDescrizione.getText())) {
            mostraErrore(areaDescrizione, erroreDescrizione, "La descrizione è obbligatoria");
            valido = false;
        }

        if (comboTipo.getValue() == null) {
            mostraErrore(comboTipo, erroreTipo, "Seleziona un tipo di issue");
            valido = false;
        }

        if (comboPriorita.getValue() == null) {
            mostraErrore(comboPriorita, errorePriorita, "Seleziona una priorità");
            valido = false;
        }

        for (javafx.scene.Node node : containerCampiDinamici.getChildren()) {
            if (node instanceof VBox box
                    && box.getChildren().size() >= 3
                    && box.getChildren().get(1) instanceof TextField tf
                    && box.getChildren().get(2) instanceof Label errLbl) {

                if (isEmpty(tf.getText())) {
                    mostraErrore(tf, errLbl, "Questo campo è obbligatorio");
                    valido = false;
                }
            }
        }

        if (!valido) {
            return;
        }

        // Recupero campi dinamici
        String istruzioni = null;
        String richiestaSpecifica = null;
        String titoloDoc = null;
        String descDoc = null;
        String richiestaFunz = null;

        String tipo = comboTipo.getValue();
        String priorita = comboPriorita.getValue();

        for (javafx.scene.Node node : containerCampiDinamici.getChildren()) {
            if (node instanceof VBox box && box.getChildren().size() >= 2
                    && box.getChildren().get(1) instanceof TextField tf) {
                String labelText = ((Label) box.getChildren().get(0)).getText();
                String val = tf.getText();

                if (labelText.contains("Istruzioni"))
                    istruzioni = val;
                else if (labelText.contains("Domanda") || labelText.contains("Richiesta specifica"))
                    richiestaSpecifica = val;
                else if (labelText.contains("Titolo Documento"))
                    titoloDoc = val;
                else if (labelText.contains("Descrizione Problema"))
                    descDoc = val;
                else if (labelText.contains("Richiesta Funzionalità"))
                    richiestaFunz = val;
            }
        }

        byte[] immagineBytes = null;
        if (fileSelezionato != null) {
            try {
                immagineBytes = java.nio.file.Files.readAllBytes(fileSelezionato.toPath());
            } catch (java.io.IOException e) {
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setTitle("Errore");
                err.setContentText("Errore lettura immagine");
                err.showAndWait();
                return;
            }
        }

        final byte[] finalImmagineBytes = immagineBytes;
        final String finalIstruzioni = istruzioni;
        final String finalRichiestaSpecifica = richiestaSpecifica;
        final String finalTitoloDoc = titoloDoc;
        final String finalDescDoc = descDoc;
        final String finalRichiestaFunz = richiestaFunz;

        Long userId = sessionManager.getUserId();
        if (userId == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setContentText("Utente non loggato. Impossibile inserire l'issue.");
            alert.showAndWait();
            return;
        }
        int idSegnalatore = userId.intValue();

        // Background Thread
        new Thread(() -> {
            try {
                RispostaIssueService risposta = apiService.inserisciIssue(
                        campoTitolo.getText(),
                        areaDescrizione.getText(),
                        finalImmagineBytes,
                        tipo,
                        "TO-DO",
                        priorita,
                        finalIstruzioni,
                        finalRichiestaSpecifica,
                        finalTitoloDoc,
                        finalDescDoc,
                        finalRichiestaFunz,
                        java.time.LocalDate.now(),
                        1, // ID Progetto hardcoded per ora
                        idSegnalatore);

                javafx.application.Platform.runLater(() -> {
                    if (risposta != null && risposta.isSuccess()) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Successo");
                        alert.setContentText("Issue inserita con successo!");
                        alert.showAndWait();
                        tornaIndietro();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Errore");
                        alert.setContentText(risposta != null ? risposta.getMessage() : "Errore sconosciuto");
                        alert.showAndWait();
                    }
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore");
                    String causeMsg = (e.getCause() != null) ? e.getCause().toString() : e.getMessage();
                    alert.setContentText("Errore di comunicazione: " + causeMsg);
                    alert.showAndWait();
                });
            }
        }).start();
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void mostraErrore(Control control, Label label, String messaggio) {
        control.getStyleClass().add(MSG_INPUT_ERROR);
        label.setText(messaggio);
        label.setManaged(true);
        label.setVisible(true);
    }

    private void resetErrorStyles() {
        rimuoviErrori(campoTitolo, erroreTitolo);
        rimuoviErrori(areaDescrizione, erroreDescrizione);
        rimuoviErrori(comboTipo, erroreTipo);
        rimuoviErrori(comboPriorita, errorePriorita);
    }

    private void rimuoviErrori(Control control, Label label) {
        control.getStyleClass().remove(MSG_INPUT_ERROR);
        label.setManaged(false);
        label.setVisible(false);
    }
}
