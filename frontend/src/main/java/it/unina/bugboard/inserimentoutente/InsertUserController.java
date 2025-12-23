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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

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
    private final InsertUserApiService insertUserApiService;
    private boolean passwordVisibile = false;
    private boolean confermaPasswordVisibile = false;
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);

    public InsertUserController(InsertUserApiService insertUserApiService) {
        this.insertUserApiService = insertUserApiService;
    }

    @FXML
    public void initialize() {
        comboSesso.getItems().addAll("M", "F");
        pulsanteCancella.disableProperty().bind(createTuttiCampiVuotiBinding());
        pulsanteRegistra.disableProperty().bind(createAlmenoUnCampoVuotoBinding().or(isLoading));
        inizializzaListenerRuolo();
        campoDataNascita.setEditable(true);

        // Define format pattern
        String pattern = "dd/MM/yyyy";
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern(pattern);

        // Set StringConverter for DatePicker
        campoDataNascita.setConverter(new javafx.util.StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try {
                        return LocalDate.parse(string, dateFormatter);
                    } catch (Exception e) {
                        return null;
                    }
                }
                return null;
            }
        });

        javafx.scene.control.TextFormatter<String> formatter = new javafx.scene.control.TextFormatter<>(change -> {
            if (!change.isContentChange()) {
                return change;
            }

            String newText = change.getControlNewText();

            if (!newText.matches("[0-9/]*") || newText.length() > 10) {
                return null;
            }

            if (change.isAdded()) {
                if (newText.length() == 2 || newText.length() == 5) {
                    change.setText(change.getText() + "/");
                    change.setCaretPosition(change.getCaretPosition() + 1);
                    change.setAnchor(change.getAnchor() + 1);
                }
            }

            return change;
        });
        campoDataNascita.getEditor().setTextFormatter(formatter);

        inizializzaTogglePassword();
        gestisciResponsive();
        configuraNavigazioneEnter();
    }

    private void gestisciResponsive() {
        if (contenitoreInserimento.getScene() != null)
            impostaListenerLarghezza(contenitoreInserimento.getScene());
        else
            contenitoreInserimento.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null)
                    impostaListenerLarghezza(newScene);
            });
    }

    private void impostaListenerLarghezza(javafx.scene.Scene scene) {
        javafx.beans.value.ChangeListener<Number> sizeListener = (obs, oldValue, newValue) -> aggiornaScala(scene);
        scene.widthProperty().addListener(sizeListener);
        scene.heightProperty().addListener(sizeListener);
        aggiornaScala(scene);
    }

    private void aggiornaScala(javafx.scene.Scene scene) {
        double width = scene.getWidth();
        double height = scene.getHeight();

        if (width < 600)
            contenitoreInserimento.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));
        else
            contenitoreInserimento.setPadding(new javafx.geometry.Insets(30, 40, 30, 40));

        double baseWidth = 1000.0;
        double baseHeight = 900.0;
        double scaleX = width / baseWidth;
        double scaleY = height / baseHeight;
        double scale = Math.min(scaleX, scaleY);
        scale = Math.clamp(scale, 1.0, 1.3);

        contenitoreInserimento.setScaleX(scale);
        contenitoreInserimento.setScaleY(scale);
    }

    private void inizializzaTogglePassword() {
        campoPasswordVisibile.textProperty().bindBidirectional(campoPassword.textProperty());
        aggiornaVisibilitaPassword(false);
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

        campoUsername.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                navigaAPasswordField();
                e.consume();
            }
        });

        javafx.event.EventHandler<KeyEvent> goToConfirm = e -> {
            if (e.getCode() == KeyCode.ENTER) {
                navigaAConfermaPasswordField();
                e.consume();
            }
        };
        campoPassword.setOnKeyPressed(goToConfirm);
        campoPasswordVisibile.setOnKeyPressed(goToConfirm);

        javafx.event.EventHandler<KeyEvent> doSubmit = e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (!pulsanteRegistra.isDisable())
                    registraUtente();
                e.consume();
            }
        };
        campoConfermaPassword.setOnKeyPressed(doSubmit);
        campoConfermaPasswordVisibile.setOnKeyPressed(doSubmit);
    }

    private void navigaAPasswordField() {
        if (campoPassword.isVisible())
            campoPassword.requestFocus();
        else
            campoPasswordVisibile.requestFocus();
    }

    private void navigaAConfermaPasswordField() {
        if (campoConfermaPassword.isVisible())
            campoConfermaPassword.requestFocus();
        else
            campoConfermaPasswordVisibile.requestFocus();
    }

    private void impostaFocusNext(Node current, Node next) {
        current.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                next.requestFocus();
                event.consume();
            }
        });
    }

    @FXML
    private void registraUtente() {
        resetErrorStyles();

        if (!eseguiValidazioni())
            return;

        InsertUserApiService.DatiAnagrafici anagrafici = new InsertUserApiService.DatiAnagrafici(
                campoNome.getText(),
                campoCognome.getText(),
                campoCodiceFiscale.getText(),
                comboSesso.getValue().charAt(0),
                campoDataNascita.getValue());

        InsertUserApiService.DatiAccesso accesso = new InsertUserApiService.DatiAccesso(
                campoUsername.getText(),
                campoPassword.getText(),
                campoEmail.getText());

        InsertUserApiService.DatiUtente dati = new InsertUserApiService.DatiUtente(
                anagrafici, accesso, toggleAdmin.isSelected());

        isLoading.set(true);
        new Thread(() -> inviaRischiestaInserimentoUtente(dati)).start();
    }

    private boolean eseguiValidazioni() {
        if (!verificaCampiObbligatori())
            return false;
        if (!verificaMaggiorenne())
            return false;
        if (!verificaPassword())
            return false;
        return verificaCodiceFiscale();
    }

    private boolean verificaMaggiorenne() {
        LocalDate data = campoDataNascita.getValue();
        if (data != null && Period.between(data, LocalDate.now()).getYears() < 18) {
            setErrorStyle(campoDataNascita);
            mostraErroreInline(erroreDataNascita, "Devi essere maggiorenne");
            return false;
        }
        return true;
    }

    private void inviaRischiestaInserimentoUtente(InsertUserApiService.DatiUtente dati) {
        try {
            RispostaInserimentoUser risposta = insertUserApiService.inserisciUtente(dati);

            Platform.runLater(() -> {
                isLoading.set(false);
                if (risposta != null && risposta.isSuccess())
                    mostraSchermataCorrettoInserimento();
                else
                    mostraErroreInserimento(risposta);
            });

        } catch (Exception e) {
            Platform.runLater(() -> {
                isLoading.set(false);
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
        if (risposta != null && risposta.getFieldErrors() != null && !risposta.getFieldErrors().isEmpty()) {
            risposta.getFieldErrors().forEach(this::mappaErroreAlCampo);
        } else {
            String messaggio = (risposta != null) ? risposta.getMessage() : "Errore durante la registrazione";
            mostraErroreInline(etichettaErrore, messaggio);
        }
    }

    private void mappaErroreAlCampo(String campo, String messaggio) {
        switch (campo.toLowerCase()) {
            case "nome":
                setErrorStyle(campoNome);
                mostraErroreInline(erroreNome, messaggio);
                break;
            case "cognome":
                setErrorStyle(campoCognome);
                mostraErroreInline(erroreCognome, messaggio);
                break;
            case "codicefiscale":
                setErrorStyle(campoCodiceFiscale);
                mostraErroreInline(erroreCodiceFiscale, messaggio);
                break;
            case "sesso":
                setErrorStyle(comboSesso);
                mostraErroreInline(erroreSesso, messaggio);
                break;
            case "datanascita":
                setErrorStyle(campoDataNascita);
                mostraErroreInline(erroreDataNascita, messaggio);
                break;
            case "email", "mail":
                setErrorStyle(campoEmail);
                mostraErroreInline(erroreEmail, messaggio);
                break;
            case "username":
                setErrorStyle(campoUsername);
                mostraErroreInline(erroreUsername, messaggio);
                break;
            case "password":
                setErrorStyle(campoPassword);
                mostraErroreInline(erroreCampoPassword, messaggio);
                break;
            default:
                mostraErroreInline(etichettaErrore, messaggio);
                break;
        }
    }

    private void mostraErroreComunicazione(Exception e) {
        it.unina.bugboard.navigation.SceneRouter.mostraAlert(
                Alert.AlertType.ERROR,
                "Errore di Comunicazione",
                "Non è stato possibile contattare il server",
                e.getMessage());
    }

    private boolean verificaCampiObbligatori() {
        boolean validi = true;

        validi &= validaSoloLettere(campoNome, erroreNome);
        validi &= validaSoloLettere(campoCognome, erroreCognome);
        validi &= validaCampoTesto(campoCodiceFiscale, erroreCodiceFiscale);
        validi &= validaComboBox(comboSesso, erroreSesso);
        validi &= validaDatePicker(campoDataNascita, erroreDataNascita);
        validi &= validaCampoTesto(campoUsername, erroreUsername);
        validi &= validaCampoTesto(campoEmail, erroreEmail);
        validi &= validaCampoPassword(campoPassword, erroreCampoPassword);
        validi &= validaCampoPassword(campoConfermaPassword, errorePassword);

        return validi;
    }

    private boolean validaSoloLettere(TextField campo, Label labelErrore) {
        if (!validaCampoTesto(campo, labelErrore))
            return false;

        String testo = campo.getText();
        if (!testo.matches("^[a-zA-Z\\sÀ-ÿ]*$")) {
            setErrorStyle(campo);
            mostraErroreInline(labelErrore, "Deve contenere solo lettere");
            return false;
        }
        return true;
    }

    private boolean validaCampoTesto(TextField campo, Label labelErrore) {
        if (isTextEmpty(campo)) {
            setErrorStyle(campo);
            mostraErroreInline(labelErrore, MSG_CAMPO_OBBLIGATORIO);
            return false;
        }
        return true;
    }

    private boolean validaCampoPassword(PasswordField campo, Label labelErrore) {
        if (campo.getText().isEmpty()) {
            setErrorStyle(campo);
            mostraErroreInline(labelErrore, MSG_CAMPO_OBBLIGATORIO);
            return false;
        }
        return true;
    }

    private boolean validaComboBox(ComboBox<String> combo, Label labelErrore) {
        if (combo.getValue() == null) {
            setErrorStyle(combo);
            mostraErroreInline(labelErrore, MSG_CAMPO_OBBLIGATORIO);
            return false;
        }
        return true;
    }

    private boolean validaDatePicker(DatePicker datePicker, Label labelErrore) {
        String testo = datePicker.getEditor().getText();
        if (testo == null || testo.trim().isEmpty()) {
            setErrorStyle(datePicker);
            mostraErroreInline(labelErrore, MSG_CAMPO_OBBLIGATORIO);
            return false;
        }

        if (datePicker.getValue() == null) {
            setErrorStyle(datePicker);
            mostraErroreInline(labelErrore, "Data non valida");
            return false;
        }
        return true;
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

    private void setErrorStyle(Control control) {
        String styleClass = "text-field-error";
        if (control instanceof ComboBox)
            styleClass = "combo-box-error";
        else if (control instanceof DatePicker)
            styleClass = "combo-box-error";

        if (!control.getStyleClass().contains(styleClass))
            control.getStyleClass().add(styleClass);
    }

    private void removeErrorStyle(Control control) {
        control.getStyleClass().removeAll("text-field-error", "combo-box-error");
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
        nascondiErrore(etichettaErrore);
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

    @FXML
    private void cancellaTutto() {
        pulisciCampi();
    }

    @FXML
    private void tornaIndietro() {
        it.unina.bugboard.navigation.SceneRouter.tornaIndietro();
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