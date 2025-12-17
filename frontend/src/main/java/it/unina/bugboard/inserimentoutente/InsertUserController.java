package it.unina.bugboard.inserimentoutente;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.Bindings;
import javafx.application.Platform;
import java.time.LocalDate;
import java.time.Period;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.Node;

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
    private TextField campoPasswordVisibile;
    @FXML
    private Button togglePassword;
    @FXML
    private PasswordField campoConfermaPassword;
    @FXML
    private TextField campoConfermaPasswordVisibile;
    @FXML
    private Button toggleConfermaPassword;
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
    private boolean passwordVisibile = false;
    private boolean confermaPasswordVisibile = false;

    public InsertUserController(InsertUserApiService insertUserApiService) {
        this.insertUserApiService = insertUserApiService;
    }

    @FXML
    public void initialize() {
        comboSesso.getItems().addAll("M", "F");
        pulsanteCancella.disableProperty().bind(createTuttiCampiVuotiBinding());
        pulsanteRegistra.disableProperty().bind(createAlmenoUnCampoVuotoBinding());
        inizializzaListenerRuolo();
        campoDataNascita.setEditable(false);
        inizializzaTogglePassword();
        gestisciResponsive();
        configuraNavigazioneEnter();
    }

    private void gestisciResponsive() {
        if (contenitoreInserimento.getScene() != null) {
            impostaListenerLarghezza(contenitoreInserimento.getScene());
        } else {
            contenitoreInserimento.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    impostaListenerLarghezza(newScene);
                }
            });
        }
    }

    private void impostaListenerLarghezza(javafx.scene.Scene scene) {
        javafx.beans.value.ChangeListener<Number> sizeListener = (obs, oldValue, newValue) -> {
            aggiornaScala(scene);
        };

        scene.widthProperty().addListener(sizeListener);
        scene.heightProperty().addListener(sizeListener);

        // Initial update
        aggiornaScala(scene);
    }

    private void aggiornaScala(javafx.scene.Scene scene) {
        double width = scene.getWidth();
        double height = scene.getHeight();

        // Padding responsive (optional here depending on design, matching existing if
        // needed)
        if (width < 600) {
            contenitoreInserimento.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));
        } else {
            contenitoreInserimento.setPadding(new javafx.geometry.Insets(30, 40, 30, 40));
        }

        // Logic for dynamic scaling
        double baseWidth = 1000.0;
        double baseHeight = 900.0;

        double scaleX = width / baseWidth;
        double scaleY = height / baseHeight;

        double scale = Math.min(scaleX, scaleY);

        // Clamp scale: min 1.0, max 1.3
        scale = Math.max(1.0, Math.min(scale, 1.3));

        contenitoreInserimento.setScaleX(scale);
        contenitoreInserimento.setScaleY(scale);
    }

    private void inizializzaTogglePassword() {
        // Main Password
        campoPasswordVisibile.textProperty().bindBidirectional(campoPassword.textProperty());
        aggiornaVisibilitaPassword(false);

        // Confirm Password
        campoConfermaPasswordVisibile.textProperty().bindBidirectional(campoConfermaPassword.textProperty());
        aggiornaVisibilitaConfermaPassword(false);
    }

    @FXML
    private void togglePasswordVisibility() {
        passwordVisibile = !passwordVisibile;
        aggiornaVisibilitaPassword(passwordVisibile);
    }

    private void aggiornaVisibilitaPassword(boolean visibile) {
        FontAwesomeIconView icon = new FontAwesomeIconView(
                visibile ? FontAwesomeIcon.EYE_SLASH : FontAwesomeIcon.EYE);
        icon.setSize("18");

        togglePassword.setGraphic(icon);
        togglePassword.setText(null);

        campoPassword.setVisible(!visibile);
        campoPasswordVisibile.setVisible(visibile);

        if (visibile) {
            campoPasswordVisibile.requestFocus();
            campoPasswordVisibile.positionCaret(campoPasswordVisibile.getText().length());
        } else {
            campoPassword.requestFocus();
            campoPassword.positionCaret(campoPassword.getText().length());
        }
    }

    @FXML
    private void toggleConfermaPasswordVisibility() {
        confermaPasswordVisibile = !confermaPasswordVisibile;
        aggiornaVisibilitaConfermaPassword(confermaPasswordVisibile);
    }

    private void aggiornaVisibilitaConfermaPassword(boolean visibile) {
        FontAwesomeIconView icon = new FontAwesomeIconView(
                visibile ? FontAwesomeIcon.EYE_SLASH : FontAwesomeIcon.EYE);
        icon.setSize("18");

        toggleConfermaPassword.setGraphic(icon);
        toggleConfermaPassword.setText(null);

        campoConfermaPassword.setVisible(!visibile);
        campoConfermaPasswordVisibile.setVisible(visibile);

        if (visibile) {
            campoConfermaPasswordVisibile.requestFocus();
            campoConfermaPasswordVisibile.positionCaret(campoConfermaPasswordVisibile.getText().length());
        } else {
            campoConfermaPassword.requestFocus();
            campoConfermaPassword.positionCaret(campoConfermaPassword.getText().length());
        }
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

    private BooleanBinding createAlmenoUnCampoVuotoBinding() {
        return isTrimmedEmpty(campoNome)
                .or(isTrimmedEmpty(campoCognome))
                .or(isTrimmedEmpty(campoCodiceFiscale))
                .or(comboSesso.valueProperty().isNull())
                .or(campoDataNascita.valueProperty().isNull())
                .or(isTrimmedEmpty(campoUsername))
                .or(isTrimmedEmpty(campoEmail))
                .or(campoPassword.textProperty().isEmpty())
                .or(campoConfermaPassword.textProperty().isEmpty());
    }

    private BooleanBinding isTrimmedEmpty(TextField textField) {
        return Bindings.createBooleanBinding(
                () -> textField.getText() == null || textField.getText().trim().isEmpty(),
                textField.textProperty());
    }

    private boolean isTextEmpty(TextField textField) {
        return textField.getText() == null || textField.getText().trim().isEmpty();
    }

    private void configuraNavigazioneEnter() {
        impostaFocusNext(campoNome, campoCognome);
        impostaFocusNext(campoCognome, campoCodiceFiscale);
        impostaFocusNext(campoCodiceFiscale, comboSesso);
        impostaFocusNext(comboSesso, campoDataNascita);
        impostaFocusNext(campoDataNascita, campoEmail);
        impostaFocusNext(campoEmail, campoUsername);

        // Gestione navigazione Username -> Password (considerando toggle visibilità)
        campoUsername.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (campoPassword.isVisible()) {
                    campoPassword.requestFocus();
                } else {
                    campoPasswordVisibile.requestFocus();
                }
                e.consume();
            }
        });

        // Gestione navigazione Password -> Conferma Password
        javafx.event.EventHandler<KeyEvent> goToConfirm = e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (campoConfermaPassword.isVisible()) {
                    campoConfermaPassword.requestFocus();
                } else {
                    campoConfermaPasswordVisibile.requestFocus();
                }
                e.consume();
            }
        };
        campoPassword.setOnKeyPressed(goToConfirm);
        campoPasswordVisibile.setOnKeyPressed(goToConfirm);

        // Gestione Conferma Password -> SUBMIT
        javafx.event.EventHandler<KeyEvent> doSubmit = e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (!pulsanteRegistra.isDisable()) {
                    registraUtente();
                }
                e.consume();
            }
        };
        campoConfermaPassword.setOnKeyPressed(doSubmit);
        campoConfermaPasswordVisibile.setOnKeyPressed(doSubmit);
    }

    private void impostaFocusNext(Node current, Node next) {
        current.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                next.requestFocus();
                event.consume();
            }
        });
    }

    // ================================================================
    // VALIDAZIONE
    // ================================================================

    @FXML
    private void registraUtente() {
        resetErrorStyles();

        if (!verificaCampiObbligatori())
            return;
        if (!verificaMaggiorenne())
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

    private boolean verificaMaggiorenne() {
        LocalDate data = campoDataNascita.getValue();
        if (data != null) {
            if (Period.between(data, LocalDate.now()).getYears() < 18) {
                setErrorStyle(campoDataNascita);
                mostraErroreInline(erroreDataNascita, "Devi essere maggiorenne");
                return false;
            }
        }
        return true;
    }

    private void inviaRischiestaInserimentoUtente(String nome, String cognome, String codiceFiscale, char sesso,
            LocalDate dataNascita, String username, String password, String email, boolean isAdmin) {
        try {
            RispostaInserimentoUser risposta = insertUserApiService.inserisciUtente(
                    nome, cognome, codiceFiscale, sesso, dataNascita, username, password, email, isAdmin);

            Platform.runLater(() -> {
                pulsanteRegistra.setDisable(false);

                if (risposta != null && risposta.isSuccess()) {
                    mostraSchermataCorrettoInserimento();
                } else {
                    mostraErroreInserimento(risposta);
                }
            });

        } catch (Exception e) {
            Platform.runLater(() -> {
                pulsanteRegistra.setDisable(false);
                mostraErroreComunicazione(e);
            });
        }
    }

    private void mostraSchermataCorrettoInserimento() {
        it.unina.bugboard.navigation.SceneRouter.mostraAlert(
                Alert.AlertType.INFORMATION,
                "Inserimento Riuscito",
                null,
                "Utente inserito con successo!");
        pulisciCampi();
    }

    private void mostraErroreInserimento(RispostaInserimentoUser risposta) {
        String messaggioErrore = costruisciMessaggioErrore(risposta);

        it.unina.bugboard.navigation.SceneRouter.mostraAlert(
                Alert.AlertType.ERROR,
                "Errore Inserimento",
                "Non è stato possibile completare la registrazione",
                messaggioErrore);
    }

    private void mostraErroreComunicazione(Exception e) {
        it.unina.bugboard.navigation.SceneRouter.mostraAlert(
                Alert.AlertType.ERROR,
                "Errore di Comunicazione",
                "Non è stato possibile contattare il server",
                e.getMessage());
    }

    private String costruisciMessaggioErrore(RispostaInserimentoUser risposta) {
        if (risposta == null) {
            return "Errore generico dal server";
        }

        StringBuilder messaggioErrore = new StringBuilder();
        messaggioErrore.append(risposta.getMessage());

        // Se ci sono errori di validazione per campo
        if (risposta.getFieldErrors() != null && !risposta.getFieldErrors().isEmpty()) {
            messaggioErrore.append("\n\nDettagli:\n");
            risposta.getFieldErrors().forEach((campo, errore) -> {
                messaggioErrore.append("• ")
                        .append(campo)
                        .append(": ")
                        .append(errore)
                        .append("\n");
            });
        }

        return messaggioErrore.toString();
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
        it.unina.bugboard.navigation.SceneRouter.cambiaScena("/it/unina/bugboard/fxml/home.fxml", 900, 800,
                "BugBoard - HomePage");
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
