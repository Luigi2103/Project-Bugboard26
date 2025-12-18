package it.unina.bugboard.inserimentoissue;

import it.unina.bugboard.navigation.SceneRouter;
import javafx.fxml.FXML;
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
    private static final String TITOLO_ERRORE = "Errore";

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
    @FXML
    private Button pulsanteInserisci;

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
        inizializzaListeners();
        inizializzaBindings();
        inizializzaUpload();
        configuraNavigazioneEnter();
    }

    private void inizializzaBindings() {
        pulsanteInserisci.disableProperty().bind(
                campoTitolo.textProperty().isEmpty()
                        .or(areaDescrizione.textProperty().isEmpty())
                        .or(comboTipo.valueProperty().isNull())
                        .or(comboPriorita.valueProperty().isNull()));
    }

    private void inizializzaComboPriorita() {
        comboPriorita.getItems().addAll("BASSA", "MEDIA", "ALTA", "MASSIMA");
        comboPriorita.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> resetErrorStyles());
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
        // Forza validazione dinamica al cambio tipo
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
            default -> throw new IllegalArgumentException("Tipo issue non supportato: " + tipo);
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

    private void configuraNavigazioneEnter() {
        campoTitolo.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                areaDescrizione.requestFocus();
                e.consume();
            }
        });

        // Per Text area usiamo CTRL+ENTER per inviare direttamente o TAB per navigare
        areaDescrizione.setOnKeyPressed(e -> {
            if (e.isControlDown() && e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                if (!pulsanteInserisci.isDisable())
                    gestisciInserimento();
                e.consume();
            }
        });

        // Combo
        comboTipo.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                comboPriorita.requestFocus();
                comboPriorita.show();
                e.consume();
            }
        });

        comboPriorita.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                if (!pulsanteInserisci.isDisable())
                    gestisciInserimento();
                e.consume();
            }
        });
    }

    @FXML
    private void tornaIndietro() {
        SceneRouter.tornaIndietro();
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
        // La validazione statica è gestita dal disableProperty
        // Validiamo solo i campi dinamici
        if (!validaCampiDinamici()) {
            mostraErroreAlert("Compila tutti i campi obbligatori");
            return;
        }

        DatiIssue dati = recuperaDatiIssue();

        Integer idSegnalatore = ottieniIdSegnalatore();
        if (idSegnalatore == null) {
            return;
        }

        inviaIssueAlServer(dati, idSegnalatore);
    }

    private boolean validaInput() {
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

        if (!validaCampiDinamici()) {
            valido = false;
        }

        return valido;
    }

    private boolean validaCampiDinamici() {
        boolean valido = true;
        for (javafx.scene.Node node : containerCampiDinamici.getChildren()) {
            if (!(node instanceof VBox box) || box.getChildren().size() < 3) {
                continue;
            }

            if (box.getChildren().get(1) instanceof TextField tf &&
                    box.getChildren().get(2) instanceof Label errLbl &&
                    isEmpty(tf.getText())) {
                mostraErrore(tf, errLbl, "Questo campo è obbligatorio");
                valido = false;
            }
        }
        return valido;
    }

    private DatiIssue recuperaDatiIssue() {
        DatiIssue dati = new DatiIssue();
        dati.titolo = campoTitolo.getText();
        dati.descrizione = areaDescrizione.getText();
        dati.tipo = comboTipo.getValue();
        dati.priorita = comboPriorita.getValue();
        dati.immagineBytes = caricaImmagine();

        recuperaCampiDinamici(dati);

        return dati;
    }

    private byte[] caricaImmagine() {
        if (fileSelezionato == null) {
            return new byte[0];
        }

        try {
            return java.nio.file.Files.readAllBytes(fileSelezionato.toPath());
        } catch (java.io.IOException e) {
            mostraErroreAlert("Errore lettura immagine");
            return new byte[0];
        }
    }

    private void recuperaCampiDinamici(DatiIssue dati) {
        for (javafx.scene.Node node : containerCampiDinamici.getChildren()) {
            if (!(node instanceof VBox box) || box.getChildren().size() < 2) {
                continue;
            }

            if (box.getChildren().get(1) instanceof TextField tf) {
                String labelText = ((Label) box.getChildren().get(0)).getText();
                String valore = tf.getText();
                assegnaCampoDinamico(dati, labelText, valore);
            }
        }
    }

    private void assegnaCampoDinamico(DatiIssue dati, String labelText, String valore) {
        if (labelText.contains("Istruzioni")) {
            dati.istruzioni = valore;
        } else if (labelText.contains("Domanda") || labelText.contains("Richiesta specifica")) {
            dati.richiestaSpecifica = valore;
        } else if (labelText.contains("Titolo Documento")) {
            dati.titoloDoc = valore;
        } else if (labelText.contains("Descrizione Problema")) {
            dati.descDoc = valore;
        } else if (labelText.contains("Richiesta Funzionalità")) {
            dati.richiestaFunz = valore;
        }
    }

    private Integer ottieniIdSegnalatore() {
        Long userId = sessionManager.getUserId();
        if (userId == null) {
            mostraErroreAlert("Utente non loggato. Impossibile inserire l'issue.");
            return null;
        }
        return userId.intValue();
    }

    private void inviaIssueAlServer(DatiIssue dati, Integer idSegnalatore) {
        new Thread(() -> {
            try {
                RispostaIssueService risposta = apiService.inserisciIssue(
                        dati.titolo,
                        dati.descrizione,
                        dati.immagineBytes,
                        dati.tipo,
                        "TO-DO",
                        dati.priorita,
                        dati.istruzioni,
                        dati.richiestaSpecifica,
                        dati.titoloDoc,
                        dati.descDoc,
                        dati.richiestaFunz,
                        java.time.LocalDate.now(),
                        1, // ID Progetto hardcoded per ora
                        idSegnalatore);

                javafx.application.Platform.runLater(() -> gestisciRisposta(risposta));

            } catch (Exception e) {
                javafx.application.Platform
                        .runLater(() -> mostraErroreAlert("Errore di comunicazione: " + getCauseMessage(e)));
            }
        }).start();
    }

    private void gestisciRisposta(RispostaIssueService risposta) {
        if (risposta != null && risposta.isSuccess()) {
            mostraSuccessoAlert("Issue inserita con successo!");
            tornaIndietro();
        } else {
            mostraErroreAlert(risposta != null ? risposta.getMessage() : "Errore sconosciuto");
        }
    }

    private String getCauseMessage(Exception e) {
        return (e.getCause() != null) ? e.getCause().toString() : e.getMessage();
    }

    private void mostraErroreAlert(String messaggio) {
        SceneRouter.mostraAlert(
                Alert.AlertType.ERROR,
                TITOLO_ERRORE,
                null,
                messaggio);
    }

    private void mostraSuccessoAlert(String messaggio) {
        SceneRouter.mostraAlert(
                Alert.AlertType.INFORMATION,
                "Successo",
                null,
                messaggio);
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

    /* -------------------------- CLASSE DATI -------------------------- */

    private static class DatiIssue {
        String titolo;
        String descrizione;
        String tipo;
        String priorita;
        byte[] immagineBytes;
        String istruzioni;
        String richiestaSpecifica;
        String titoloDoc;
        String descDoc;
        String richiestaFunz;
    }
}