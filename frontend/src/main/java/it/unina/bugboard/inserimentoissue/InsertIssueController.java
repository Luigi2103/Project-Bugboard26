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
    @FXML
    private Button pulsanteRimuoviImmagine;

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
        inizializzaBindings();
        inizializzaUpload();
        configuraNavigazioneEnter();
        pulsanteRimuoviImmagine.setOnMouseClicked(javafx.scene.input.MouseEvent::consume);
    }

    private void inizializzaBindings() {
        aggiornaStatoBottone();
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
        campoTitolo.textProperty().addListener((obs, old, neu) -> {
            resetErrorStyles();
            aggiornaStatoBottone();
        });
        areaDescrizione.textProperty().addListener((obs, old, neu) -> {
            resetErrorStyles();
            aggiornaStatoBottone();
        });
        comboTipo.valueProperty().addListener((obs, old, neu) -> aggiornaStatoBottone());
        comboPriorita.valueProperty().addListener((obs, old, neu) -> aggiornaStatoBottone());
    }

    private void inizializzaUpload() {
        areaUploadImmagine.setOnMouseClicked(event -> selezionaImmagine());
    }

    /* -------------------------- CAMPi DINAMICI -------------------------- */

    private void aggiornaCampiDinamici(String tipo) {
        containerCampiDinamici.getChildren().clear();
        if (tipo == null)
            return;

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
            aggiornaStatoBottone();
        });

        VBox box = new VBox(5, label, textField, errorLabel);


        textField.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                int currentIndex = containerCampiDinamici.getChildren().indexOf(box);
                if (currentIndex >= 0 && currentIndex < containerCampiDinamici.getChildren().size() - 1) {
                    javafx.scene.Node nextNode = containerCampiDinamici.getChildren().get(currentIndex + 1);
                    if (nextNode instanceof VBox nextBox && nextBox.getChildren().size() >= 2) {
                        nextBox.getChildren().get(1).requestFocus();
                    }
                } else {
                    if (!pulsanteInserisci.isDisable()) {
                        gestisciInserimento();
                    }
                }
                e.consume();
            }
        });

        containerCampiDinamici.getChildren().add(box);
    }



    private void selezionaImmagine() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona Immagine");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        Stage stage = (Stage) areaUploadImmagine.getScene().getWindow();
        fileSelezionato = fileChooser.showOpenDialog(stage);

        if (fileSelezionato != null) {
            labelNomeFile.setText(fileSelezionato.getName());
            pulsanteRimuoviImmagine.setVisible(true);
            pulsanteRimuoviImmagine.setManaged(true);
        }
    }

    @FXML
    private void rimuoviImmagine() {
        fileSelezionato = null;
        labelNomeFile.setText("Clicca per caricare un'immagine");
        pulsanteRimuoviImmagine.setVisible(false);
        pulsanteRimuoviImmagine.setManaged(false);
    }



    private void configuraNavigazioneEnter() {
        campoTitolo.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                areaDescrizione.requestFocus();
                e.consume();
            }
        });

        areaDescrizione.setOnKeyPressed(e -> {
            if (e.isControlDown() && e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                if (!pulsanteInserisci.isDisable())
                    gestisciInserimento();
                e.consume();
            }
        });

        comboTipo.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                comboPriorita.requestFocus();
                comboPriorita.show();
                e.consume();
            }
        });

        comboPriorita.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                if (!containerCampiDinamici.getChildren().isEmpty()) {
                    javafx.scene.Node node = containerCampiDinamici.getChildren().get(0);
                    if (node instanceof VBox box && box.getChildren().size() > 1) {
                        box.getChildren().get(1).requestFocus();
                    }
                } else if (!pulsanteInserisci.isDisable()) {
                    gestisciInserimento();
                }
                e.consume();
            }
        });
    }

    @FXML
    private void tornaIndietro() {
        SceneRouter.tornaIndietro();
    }



    @FXML
    private void pulisciCampi() {
        campoTitolo.clear();
        areaDescrizione.clear();
        comboTipo.getSelectionModel().clearSelection();
        comboPriorita.getSelectionModel().clearSelection();
        containerCampiDinamici.getChildren().clear();

        labelNomeFile.setText("Clicca per caricare un'immagine");
        fileSelezionato = null;
        pulsanteRimuoviImmagine.setVisible(false);
        pulsanteRimuoviImmagine.setManaged(false);

        resetErrorStyles();
    }



    @FXML
    private void gestisciInserimento() {
        if (!validaCampiDinamici()) {
            mostraErroreAlert("Compila tutti i campi obbligatori");
            return;
        }

        DatiIssueInterni dati = recuperaDatiIssue();

        Integer idSegnalatore = ottieniIdSegnalatore();
        if (idSegnalatore == null)
            return;

        inviaIssueAlServer(dati, idSegnalatore);
    }

    private boolean validaCampiDinamici() {
        boolean valido = true;
        for (javafx.scene.Node node : containerCampiDinamici.getChildren()) {
            if (!(node instanceof VBox box) || box.getChildren().size() < 3)
                continue;

            if (box.getChildren().get(1) instanceof TextField tf &&
                    box.getChildren().get(2) instanceof Label errLbl &&
                    isEmpty(tf.getText())) {
                mostraErrore(tf, errLbl, "Questo campo è obbligatorio");
                valido = false;
            }
        }
        return valido;
    }

    private DatiIssueInterni recuperaDatiIssue() {
        DatiIssueInterni dati = new DatiIssueInterni();
        dati.titolo = campoTitolo.getText();
        dati.descrizione = areaDescrizione.getText();
        dati.tipo = comboTipo.getValue();
        dati.priorita = comboPriorita.getValue();
        dati.immagineBytes = caricaImmagine();

        recuperaCampiDinamici(dati);

        return dati;
    }

    private byte[] caricaImmagine() {
        if (fileSelezionato == null)
            return new byte[0];

        try {
            return java.nio.file.Files.readAllBytes(fileSelezionato.toPath());
        } catch (java.io.IOException e) {
            mostraErroreAlert("Errore lettura immagine");
            return new byte[0];
        }
    }

    private void recuperaCampiDinamici(DatiIssueInterni dati) {
        for (javafx.scene.Node node : containerCampiDinamici.getChildren()) {
            if (!(node instanceof VBox box) || box.getChildren().size() < 2)
                continue;

            if (box.getChildren().get(1) instanceof TextField tf) {
                String labelText = ((Label) box.getChildren().get(0)).getText();
                String valore = tf.getText();
                assegnaCampoDinamico(dati, labelText, valore);
            }
        }
    }

    private void assegnaCampoDinamico(DatiIssueInterni dati, String labelText, String valore) {
        if (labelText.contains("Istruzioni"))
            dati.istruzioni = valore;
        else if (labelText.contains("Domanda") || labelText.contains("Richiesta specifica"))
            dati.richiestaSpecifica = valore;
        else if (labelText.contains("Titolo Documento"))
            dati.titoloDoc = valore;
        else if (labelText.contains("Descrizione Problema"))
            dati.descDoc = valore;
        else if (labelText.contains("Richiesta Funzionalità"))
            dati.richiestaFunz = valore;
    }

    private Integer ottieniIdSegnalatore() {
        Long userId = sessionManager.getUserId();
        if (userId == null) {
            mostraErroreAlert("Utente non loggato. Impossibile inserire l'issue.");
            return null;
        }
        return userId.intValue();
    }

    private void inviaIssueAlServer(DatiIssueInterni dati, Integer idSegnalatore) {
        new Thread(() -> {
            try {
                InsertIssueApiService.DatiBase base = new InsertIssueApiService.DatiBase(
                        dati.titolo,
                        dati.descrizione,
                        dati.immagineBytes);

                InsertIssueApiService.DatiTipologia tipologia = new InsertIssueApiService.DatiTipologia(
                        dati.tipo,
                        "TO-DO",
                        dati.priorita,
                        dati.istruzioni,
                        dati.richiestaSpecifica,
                        dati.richiestaFunz);

                InsertIssueApiService.DatiDocumento documento = new InsertIssueApiService.DatiDocumento(
                        dati.titoloDoc,
                        dati.descDoc);

                InsertIssueApiService.DatiContesto contesto = new InsertIssueApiService.DatiContesto(
                        java.time.LocalDate.now(),
                        1,
                        idSegnalatore);

                InsertIssueApiService.DatiIssue datiIssue = new InsertIssueApiService.DatiIssue(
                        base, tipologia, documento, contesto);

                RispostaIssueService risposta = apiService.inserisciIssue(datiIssue);

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
        } else if (risposta != null && risposta.getFieldErrors() != null && !risposta.getFieldErrors().isEmpty()) {
            risposta.getFieldErrors().forEach(this::mappaErroreAlCampo);
        } else {
            mostraErroreAlert(risposta != null ? risposta.getMessage() : "Errore sconosciuto");
        }
    }

    private void mappaErroreAlCampo(String campo, String messaggio) {
        switch (campo.toLowerCase()) {
            case "titolo":
                mostraErrore(campoTitolo, erroreTitolo, messaggio);
                break;
            case "descrizione":
                mostraErrore(areaDescrizione, erroreDescrizione, messaggio);
                break;
            case "tipologia":
                mostraErrore(comboTipo, erroreTipo, messaggio);
                break;
            case "priorita":
                mostraErrore(comboPriorita, errorePriorita, messaggio);
                break;
            default:
                // Se è un campo dinamico, cerchiamo nel container
                cercaErroreInCampiDinamici(campo, messaggio);
                break;
        }
    }

    private void cercaErroreInCampiDinamici(String campo, String messaggio) {
        for (javafx.scene.Node node : containerCampiDinamici.getChildren()) {
            if (node instanceof VBox box && box.getChildren().size() >= 3) {
                Label lbl = (Label) box.getChildren().get(0);
                if (lbl.getText().toLowerCase().contains(campo.toLowerCase())) {
                    TextField tf = (TextField) box.getChildren().get(1);
                    Label errLbl = (Label) box.getChildren().get(2);
                    mostraErrore(tf, errLbl, messaggio);
                }
            }
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



    private void aggiornaStatoBottone() {
        boolean staticiValidi = !isEmpty(campoTitolo.getText()) &&
                !isEmpty(areaDescrizione.getText()) &&
                comboTipo.getValue() != null &&
                comboPriorita.getValue() != null;

        if (!staticiValidi) {
            pulsanteInserisci.setDisable(true);
            return;
        }

        boolean dinamiciValidi = true;
        for (javafx.scene.Node node : containerCampiDinamici.getChildren()) {
            if (node instanceof VBox box && box.getChildren().size() >= 2
                    && box.getChildren().get(1) instanceof TextField tf) {
                if (isEmpty(tf.getText())) {
                    dinamiciValidi = false;
                    break;
                }
            }
        }

        pulsanteInserisci.setDisable(!dinamiciValidi);
    }

    private static class DatiIssueInterni {
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