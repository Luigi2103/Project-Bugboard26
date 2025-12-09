package it.unina.bugboard.inserimentoIssue;

import it.unina.bugboard.navigation.SceneRouter;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class InsertIssueController {

    @FXML
    private TextField campoTitolo;

    @FXML
    private TextArea areaDescrizione;

    @FXML
    private ComboBox<String> comboTipo;

    @FXML
    private VBox containerCampiDinamici;

    @FXML
    private VBox areaUploadImmagine;

    @FXML
    private ImageView anteprimaImmagine;

    @FXML
    private Label labelNomeFile;

    @FXML
    private Label erroreTitolo;
    @FXML
    private Label erroreDescrizione;
    @FXML
    private Label erroreTipo;

    private File fileSelezionato;

    @FXML
    public void initialize() {
        // Popola il ComboBox
        comboTipo.getItems().addAll("BUG", "QUESTION", "DOCUMENTATION", "FEATURE");

        // Listener per il cambio di selezione
        comboTipo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            aggiornaCampiDinamici(newValue);
            resetErrorStyles();
        });

        // Listener per rimuovere errori alla modifica
        campoTitolo.textProperty().addListener((obs, old, newVal) -> resetErrorStyles());
        areaDescrizione.textProperty().addListener((obs, old, newVal) -> resetErrorStyles());

        // Gestione click su area upload
        areaUploadImmagine.setOnMouseClicked(event -> selezionaImmagine());
    }

    private void aggiornaCampiDinamici(String tipo) {
        containerCampiDinamici.getChildren().clear();

        if (tipo == null)
            return;

        switch (tipo) {
            case "BUG":
                aggiungiCampoTesto("Istruzioni per riprodurre", "Inserisci i passaggi...");
                break;
            case "QUESTION":
                aggiungiCampoTesto("Richiesta specifica", "Qual è la tua domanda?");
                break;
            case "DOCUMENTATION":
                aggiungiCampoTesto("Titolo Documento", "Titolo del documento...");
                aggiungiCampoTesto("Descrizione Problema", "Descrivi il problema nella documentazione...");
                break;
            case "FEATURE":
                aggiungiCampoTesto("Richiesta Funzionalità", "Descrivi la funzionalità richiesta...");
                break;
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

    private void selezionaImmagine() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona Immagine");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        Stage stage = (Stage) areaUploadImmagine.getScene().getWindow();
        fileSelezionato = fileChooser.showOpenDialog(stage);

        if (fileSelezionato != null) {
            Image image = new Image(fileSelezionato.toURI().toString());
            anteprimaImmagine.setImage(image);
            anteprimaImmagine.setManaged(true);
            anteprimaImmagine.setVisible(true);
            labelNomeFile.setText(fileSelezionato.getName());
        }
    }

    @FXML
    private void tornaIndietro() {
        SceneRouter.cambiaScena("/it/unina/bugboard/fxml/home.fxml",
                1200, 800, "BugBoard - Home");
    }

    @FXML
    private void gestisciInserimento() {
        resetErrorStyles();
        boolean isValid = true;

        String titolo = campoTitolo.getText();
        String descrizione = areaDescrizione.getText();
        String tipo = comboTipo.getValue();

        if (titolo == null || titolo.trim().isEmpty()) {
            setErrorStyle(campoTitolo, "text-field-error");
            mostraErroreInline(erroreTitolo, "Il titolo è obbligatorio");
            isValid = false;
        }

        if (descrizione == null || descrizione.trim().isEmpty()) {
            setErrorStyle(areaDescrizione, "text-area-error");
            mostraErroreInline(erroreDescrizione, "La descrizione è obbligatoria");
            isValid = false;
        }

        if (tipo == null) {
            setErrorStyle(comboTipo, "combo-box-error");
            mostraErroreInline(erroreTipo, "Seleziona un tipo di issue");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Qui andrebbe la logica per salvare l'issue
        System.out.println("Inserimento Issue: " + titolo + " [" + tipo + "]");
        // TODO: Implementare salvataggio effettivo

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText("Issue inserita con successo!");
        alert.showAndWait();

        tornaIndietro();
    }

    private void setErrorStyle(Control control, String styleClass) {
        if (!control.getStyleClass().contains(styleClass)) {
            control.getStyleClass().add(styleClass);
        }
    }

    private void removeErrorStyle(Control control, String styleClass) {
        control.getStyleClass().remove(styleClass);
    }

    private void resetErrorStyles() {
        removeErrorStyle(campoTitolo, "text-field-error");
        removeErrorStyle(areaDescrizione, "text-area-error");
        removeErrorStyle(comboTipo, "combo-box-error");

        nascondiErrore(erroreTitolo);
        nascondiErrore(erroreDescrizione);
        nascondiErrore(erroreTipo);
    }

    private void mostraErroreInline(Label label, String messaggio) {
        label.setText(messaggio);
        label.setManaged(true);
        label.setVisible(true);
    }

    private void nascondiErrore(Label label) {
        label.setManaged(false);
        label.setVisible(false);
    }
}
