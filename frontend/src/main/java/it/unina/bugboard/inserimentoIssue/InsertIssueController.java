package it.unina.bugboard.inserimentoIssue;

import it.unina.bugboard.navigation.SceneRouter;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class InsertIssueController {

    private static final String ISSUE_BUG = "BUG";
    private static final String ISSUE_QUESTION = "QUESTION";
    private static final String ISSUE_DOC = "DOCUMENTATION";
    private static final String ISSUE_FEATURE = "FEATURE";

    @FXML private TextField campoTitolo;
    @FXML private TextArea areaDescrizione;
    @FXML private ComboBox<String> comboTipo;

    @FXML private VBox containerCampiDinamici;
    @FXML private VBox areaUploadImmagine;
    @FXML private ImageView anteprimaImmagine;

    @FXML private Label labelNomeFile;
    @FXML private Label erroreTitolo;
    @FXML private Label erroreDescrizione;
    @FXML private Label erroreTipo;

    @FXML private FlowPane tagsContainer;
    @FXML private TextField campoTags;

    private File fileSelezionato;

    @FXML
    public void initialize() {
        inizializzaComboTipo();
        inizializzaListeners();
        inizializzaUpload();
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

        campoTags.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                confermaTag();
                event.consume();
            }
        });

        campoTags.focusedProperty().addListener((obs, old, focused) -> {
            if (!focused.booleanValue()) {
                confermaTag();
            }
        });
    }

    private void inizializzaUpload() {
        areaUploadImmagine.setOnMouseClicked(event -> selezionaImmagine());
    }

    /* -------------------------- TAGS -------------------------- */

    private void confermaTag() {
        String testo = campoTags.getText().trim();
        if (!testo.isEmpty()) {
            aggiungiTag(testo);
            campoTags.clear();
        }
    }

    private void aggiungiTag(String testo) {
        HBox chip = new HBox(5);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.getStyleClass().add("tag-chip");

        Label label = new Label(testo);
        label.getStyleClass().add("tag-label-text");

        Button closeBtn = new Button("×");
        closeBtn.getStyleClass().add("tag-close-button");
        closeBtn.setOnAction(e -> tagsContainer.getChildren().remove(chip));

        chip.getChildren().addAll(label, closeBtn);

        int insertIndex = Math.max(0, tagsContainer.getChildren().size() - 1);
        tagsContainer.getChildren().add(insertIndex, chip);
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

        VBox box = new VBox(5, label, textField);
        containerCampiDinamici.getChildren().add(box);
    }

    /* -------------------------- UPLOAD IMMAGINE -------------------------- */

    private void selezionaImmagine() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona Immagine");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) areaUploadImmagine.getScene().getWindow();
        fileSelezionato = fileChooser.showOpenDialog(stage);

        if (fileSelezionato != null) {
            Image img = new Image(fileSelezionato.toURI().toString());
            anteprimaImmagine.setImage(img);
            anteprimaImmagine.setVisible(true);
            anteprimaImmagine.setManaged(true);
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
        containerCampiDinamici.getChildren().clear();

        anteprimaImmagine.setImage(null);
        anteprimaImmagine.setVisible(false);
        anteprimaImmagine.setManaged(false);
        labelNomeFile.setText("Clicca per caricare un'immagine");
        fileSelezionato = null;

        campoTags.clear();
        tagsContainer.getChildren().removeIf(HBox.class::isInstance);

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

        if (!valido) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setContentText("Issue inserita con successo!");
        alert.showAndWait();

        tornaIndietro();
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void mostraErrore(Control control, Label label, String messaggio) {
        control.getStyleClass().add("input-error");
        label.setText(messaggio);
        label.setManaged(true);
        label.setVisible(true);
    }

    private void resetErrorStyles() {
        rimuoviErrori(campoTitolo, erroreTitolo);
        rimuoviErrori(areaDescrizione, erroreDescrizione);
        rimuoviErrori(comboTipo, erroreTipo);
    }

    private void rimuoviErrori(Control control, Label label) {
        control.getStyleClass().remove("input-error");
        label.setManaged(false);
        label.setVisible(false);
    }
}
