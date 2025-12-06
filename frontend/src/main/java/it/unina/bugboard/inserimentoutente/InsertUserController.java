package it.unina.bugboard.inserimentoutente;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.Bindings;
import javafx.application.Platform;
import java.time.LocalDate;

public class InsertUserController {

    @FXML
    private VBox contenitoreInserimento;
    @FXML
    private TextField campoNome;
    @FXML
    private TextField campoCognome;
    @FXML
    private TextField campoCodiceFiscale;
    @FXML
    private ComboBox<String> comboSesso;
    @FXML
    private DatePicker campoDataNascita;
    @FXML
    private TextField campoUsername;
    @FXML
    private TextField campoEmail;
    @FXML
    private PasswordField campoPassword;
    @FXML
    private PasswordField campoConfermaPassword;
    @FXML
    private Button pulsanteRegistra;
    @FXML
    private Button pulsanteCancella;
    @FXML
    private Button pulsanteIndietro;
    @FXML
    private Label etichettaErrore;
    @FXML
    private ToggleGroup gruppoRuolo;
    @FXML
    private ToggleButton toggleUtente;
    @FXML
    private ToggleButton toggleAdmin;

    @FXML
    private Label erroreCodiceFiscale;
    @FXML
    private Label errorePassword;
    @FXML
    private Label erroreNome;
    @FXML
    private Label erroreCognome;
    @FXML
    private Label erroreSesso;
    @FXML
    private Label erroreDataNascita;
    @FXML
    private Label erroreUsername;
    @FXML
    private Label erroreEmail;
    @FXML
    private Label erroreCampoPassword;

    private static final String MSG_CAMPO_OBBLIGATORIO = "Campo obbligatorio";
    private static final String MSG_TXTFIELD_OBBLIGATORIO = "text-field-error";
    private final InsertUserApiService insertUserApiService;

    public InsertUserController(InsertUserApiService insertUserApiService) {
        this.insertUserApiService = insertUserApiService;
    }

    @FXML
    public void initialize() {
        comboSesso.getItems().addAll("M", "F");
        pulsanteCancella.disableProperty().bind(createTuttiCampiVuotiBinding());
        inizializzaListenerRuolo();
        campoDataNascita.setEditable(false);
    }

    private void inizializzaListenerRuolo() {
        gruppoRuolo.selectedToggleProperty().addListener((obs, vecchio, nuovo) -> {
            if (nuovo == null)
                vecchio.setSelected(true);
        });
    }

    private BooleanBinding createTuttiCampiVuotiBinding() {
        return isTrimmedEmpty(campoNome)
                .and(isTrimmedEmpty(campoCognome))
                .and(isTrimmedEmpty(campoCodiceFiscale))
                .and(comboSesso.valueProperty().isNull())
                .and(campoDataNascita.valueProperty().isNull())
                .and(isTrimmedEmpty(campoUsername))
                .and(isTrimmedEmpty(campoEmail))
                .and(campoPassword.textProperty().isEmpty())
                .and(campoConfermaPassword.textProperty().isEmpty());
    }

    private BooleanBinding isTrimmedEmpty(TextField textField) {
        return Bindings.createBooleanBinding(
                () -> textField.getText() == null || textField.getText().trim().isEmpty(),
                textField.textProperty());
    }

    private boolean isTextEmpty(TextField textField) {
        return textField.getText() == null || textField.getText().trim().isEmpty();
    }

    // ================================================================
    // VALIDAZIONE
    // ================================================================

    @FXML
    private void registraUtente() {
        resetErrorStyles();

        if (!verificaCampiObbligatori())
            return;
        if (!verificaPassword())
            return;
        if (!verificaCodiceFiscale())
            return;

        // Dati input
        String nome = campoNome.getText();
        String cognome = campoCognome.getText();
        String codiceFiscale = campoCodiceFiscale.getText();
        char sesso = comboSesso.getValue().charAt(0);
        LocalDate dataNascita = campoDataNascita.getValue();
        String username = campoUsername.getText();
        String email = campoEmail.getText();
        String password = campoPassword.getText();
        boolean isAdmin = toggleAdmin.isSelected();

        // Disabilita pulsante durante la richiesta
        pulsanteRegistra.setDisable(true);

        new Thread(() -> inviaRischiestaInserimentoUtente(nome, cognome, codiceFiscale, sesso, dataNascita, username,
                password, email, isAdmin)).start();
    }

    private void inviaRischiestaInserimentoUtente(String nome, String cognome, String codiceFiscale, char sesso,
                                                  LocalDate dataNascita, String username, String password, String email, boolean isAdmin) {
        try {
            RispostaInserimentoUser risposta = insertUserApiService.inserisciUtente(nome, cognome, codiceFiscale, sesso,
                    dataNascita, username, password, email, isAdmin);

            Platform.runLater(() -> {
                pulsanteRegistra.setDisable(false);
                if (risposta != null && risposta.isSuccess()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Inserimento Riuscito");
                    alert.setHeaderText(null);
                    alert.setContentText("Utente inserito con successo!");
                    alert.showAndWait();
                    pulisciCampi();
                } else {
                    String msg = (risposta != null && risposta.getMessage() != null) ? risposta.getMessage()
                            : "Errore generico dal server";
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore Inserimento");
                    alert.setHeaderText(null);
                    alert.setContentText(msg);
                    alert.showAndWait();
                }
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                pulsanteRegistra.setDisable(false);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore di Comunicazione");
                alert.setHeaderText("Non è stato possibile contattare il server.");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            });
        }
    }

    private boolean verificaCampiObbligatori() {
        boolean validi = true;

        if (isTextEmpty(campoNome)) {
            setErrorStyle(campoNome);
            mostraErroreInline(erroreNome, MSG_CAMPO_OBBLIGATORIO);
            validi = false;
        }

        if (isTextEmpty(campoCognome)) {
            setErrorStyle(campoCognome);
            mostraErroreInline(erroreCognome, MSG_CAMPO_OBBLIGATORIO);
            validi = false;
        }

        if (isTextEmpty(campoCodiceFiscale)) {
            setErrorStyle(campoCodiceFiscale);
            mostraErroreInline(erroreCodiceFiscale, MSG_CAMPO_OBBLIGATORIO);
            validi = false;
        }

        if (comboSesso.getValue() == null) {
            setErrorStyle(comboSesso);
            mostraErroreInline(erroreSesso, MSG_CAMPO_OBBLIGATORIO);
            validi = false;
        }

        if (campoDataNascita.getValue() == null) {
            setErrorStyle(campoDataNascita);
            mostraErroreInline(erroreDataNascita, MSG_CAMPO_OBBLIGATORIO);
            validi = false;
        }

        if (isTextEmpty(campoUsername)) {
            setErrorStyle(campoUsername);
            mostraErroreInline(erroreUsername, MSG_CAMPO_OBBLIGATORIO);
            validi = false;
        }

        if (isTextEmpty(campoEmail)) {
            setErrorStyle(campoEmail);
            mostraErroreInline(erroreEmail, MSG_CAMPO_OBBLIGATORIO);
            validi = false;
        }

        if (campoPassword.getText().isEmpty()) {
            setErrorStyle(campoPassword);
            mostraErroreInline(erroreCampoPassword, MSG_CAMPO_OBBLIGATORIO);
            validi = false;
        }

        if (campoConfermaPassword.getText().isEmpty()) {
            setErrorStyle(campoConfermaPassword);
            mostraErroreInline(errorePassword, MSG_CAMPO_OBBLIGATORIO);
            validi = false;
        }

        return validi;
    }

    private boolean verificaPassword() {
        String p1 = campoPassword.getText();
        String p2 = campoConfermaPassword.getText();

        if (!p1.equals(p2)) {
            setErrorStyle(campoPassword);
            setErrorStyle(campoConfermaPassword);
            mostraErroreInline(errorePassword, "Le password non coincidono");
            return false;
        }

        return true;
    }

    private boolean verificaCodiceFiscale() {
        String cf = campoCodiceFiscale.getText();

        if (cf.length() != 16) {
            setErrorStyle(campoCodiceFiscale);
            mostraErroreInline(erroreCodiceFiscale, "Il Codice Fiscale deve essere di 16 caratteri");
            return false;
        }

        return true;
    }

    // ================================================================
    // ERRORI UI
    // ================================================================

    private void setErrorStyle(Control control) {
        if (!control.getStyleClass().contains(MSG_TXTFIELD_OBBLIGATORIO)) {
            control.getStyleClass().add(MSG_TXTFIELD_OBBLIGATORIO);
        }
    }

    private void removeErrorStyle(Control control) {
        control.getStyleClass().remove(MSG_TXTFIELD_OBBLIGATORIO);
    }

    private void resetErrorStyles() {
        Control[] campi = {
                campoNome, campoCognome, campoCodiceFiscale,
                comboSesso, campoDataNascita, campoUsername, campoEmail,
                campoPassword, campoConfermaPassword
        };

        Label[] labels = {
                erroreNome, erroreCognome, erroreCodiceFiscale,
                erroreSesso, erroreDataNascita, erroreUsername, erroreEmail,
                erroreCampoPassword, errorePassword
        };

        for (Control c : campi)
            removeErrorStyle(c);
        for (Label l : labels)
            nascondiErrore(l);
    }

    private void mostraErroreInline(Label label, String messaggio) {
        label.setText(messaggio);
        label.setVisible(true);
        label.setManaged(true);
    }

    private void nascondiErrore(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }

    // ================================================================
    // ALTRO
    // ================================================================

    @FXML
    private void cancellaTutto() {
        pulisciCampi();
    }

    @FXML
    private void tornaIndietro() {
        it.unina.bugboard.navigation.SceneRouter.cambiaScena("/it/unina/bugboard/fxml/login.fxml", 900, 800,
                "BugBoard - Login");
    }

    private void pulisciCampi() {
        campoNome.clear();
        campoCognome.clear();
        campoCodiceFiscale.clear();
        comboSesso.setValue(null);
        campoDataNascita.setValue(null);
        campoUsername.clear();
        campoEmail.clear();
        campoPassword.clear();
        campoConfermaPassword.clear();
        resetErrorStyles();
        toggleUtente.setSelected(true);
    }
}
