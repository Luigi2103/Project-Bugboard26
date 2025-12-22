package it.unina.bugboard.recovery;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.application.Platform;
import it.unina.bugboard.navigation.SceneRouter;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.util.logging.Logger;
import java.net.URL;
import java.util.ResourceBundle;

public class RecoveryController implements Initializable {

    @FXML
    private VBox contenitoreRecovery;
    @FXML
    private VBox boxForm;
    @FXML
    private ImageView logoImmagine;

    @FXML
    private TextField campoUsername;
    @FXML
    private Label erroreUsername;

    // Password Vecchia
    @FXML
    private PasswordField campoPassword;
    @FXML
    private TextField campoPasswordVisibile;
    @FXML
    private Button togglePassword;
    @FXML
    private Label errorePassword;

    // Password Nuova
    @FXML
    private PasswordField campoNuovaPassword;
    @FXML
    private TextField campoNuovaPasswordVisibile;
    @FXML
    private Button toggleNuovaPassword;
    @FXML
    private Label erroreNuovaPassword;

    @FXML
    private Button pulsanteCancella;
    @FXML
    private Button pulsanteIndietro;
    @FXML
    private Button pulsanteConferma;

    private boolean passwordVisibile = false;
    private boolean nuovaPasswordVisibile = false;
    private static final String LOGIN_FXML = "/it/unina/bugboard/fxml/login.fxml";
    private static final int LOGIN_WIDTH = 900;
    private static final int LOGIN_HEIGHT = 930;
    private static final String LOGIN_TITLE = "BugBoard - Login";
    private static final String ERR_MSG = "text-field-error";

    private static final Logger LOGGER = Logger.getLogger(RecoveryController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        caricaLogo();
        inizializzaBindings();
        inizializzaTogglePassword();
        inizializzaBindingCancella();
        animaIngresso();
        gestisciResponsive();
        Platform.runLater(() -> contenitoreRecovery.requestFocus());
        configuraNavigazioneEnter();
    }

    private void gestisciResponsive() {
        if (contenitoreRecovery.getScene() != null) {
            impostaListenerLarghezza(contenitoreRecovery.getScene());
        } else {
            contenitoreRecovery.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null)
                    impostaListenerLarghezza(newScene);
            });
        }
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

        if (width < 600) {
            contenitoreRecovery.setPadding(new javafx.geometry.Insets(30, 20, 30, 20));
        } else {
            contenitoreRecovery.setPadding(new javafx.geometry.Insets(30, 40, 30, 40));
        }

        // Logica di scaling
        double baseWidth = 1200.0;
        double baseHeight = 800.0;

        double scaleX = width / baseWidth;
        double scaleY = height / baseHeight;

        double scale = Math.min(scaleX, scaleY);

        // Usa Math.clamp (Java 21+)
        scale = Math.clamp(scale, 1.0, 1.3);

        contenitoreRecovery.setScaleX(scale);
        contenitoreRecovery.setScaleY(scale);
    }

    private void caricaLogo() {
        try {
            URL logoUrl = getClass().getResource("/it/unina/bugboard/foto/logoCompleto.png");
            if (logoUrl != null) {
                Image logo = new Image(logoUrl.toExternalForm());
                logoImmagine.setImage(logo);
            } else {
                LOGGER.warning("Logo non trovato nel path specificato");
            }
        } catch (Exception e) {
            LOGGER.info("Errore caricamento logo: " + e.getMessage());
        }
    }

    private void inizializzaBindings() {
        campoUsername.prefWidthProperty().bind(boxForm.widthProperty());
        pulsanteConferma.disableProperty().bind(
                campoUsername.textProperty().isEmpty()
                        .or(campoPassword.textProperty().isEmpty())
                        .or(campoNuovaPassword.textProperty().isEmpty()));
    }

    private void inizializzaTogglePassword() {
        campoPasswordVisibile.textProperty().bindBidirectional(campoPassword.textProperty());
        campoNuovaPasswordVisibile.textProperty().bindBidirectional(campoNuovaPassword.textProperty());

        aggiornaVisibilitaPassword(false);
        aggiornaVisibilitaNuovaPassword(false);
    }

    @FXML
    private void togglePasswordVisibility() {
        passwordVisibile = !passwordVisibile;
        aggiornaVisibilitaPassword(passwordVisibile);
    }

    @FXML
    private void toggleNuovaPasswordVisibility() {
        nuovaPasswordVisibile = !nuovaPasswordVisibile;
        aggiornaVisibilitaNuovaPassword(nuovaPasswordVisibile);
    }

    private void aggiornaVisibilitaPassword(boolean visibile) {
        aggiornaVisibilitaCampo(visibile, togglePassword, campoPassword, campoPasswordVisibile);
    }

    private void aggiornaVisibilitaNuovaPassword(boolean visibile) {
        aggiornaVisibilitaCampo(visibile, toggleNuovaPassword, campoNuovaPassword, campoNuovaPasswordVisibile);
    }

    // Metodo estratto per eliminare duplicazione
    private void aggiornaVisibilitaCampo(boolean visibile, Button toggleButton,
                                         PasswordField campoPassword, TextField campoVisibile) {
        aggiornaIconaToggle(toggleButton, visibile);

        campoPassword.setVisible(!visibile);
        campoVisibile.setVisible(visibile);

        if (visibile) {
            campoVisibile.requestFocus();
            campoVisibile.positionCaret(campoVisibile.getText().length());
        } else {
            campoPassword.requestFocus();
            campoPassword.positionCaret(campoPassword.getText().length());
        }
    }

    private void aggiornaIconaToggle(Button toggleButton, boolean visibile) {
        FontAwesomeIconView icon = new FontAwesomeIconView(
                visibile ? FontAwesomeIcon.EYE_SLASH : FontAwesomeIcon.EYE);
        icon.setSize("18");
        toggleButton.setGraphic(icon);
        toggleButton.setText(null);
    }

    @FXML
    private void gestisciAnnulla() {
        SceneRouter.cambiaScena(LOGIN_FXML, LOGIN_WIDTH, LOGIN_HEIGHT, LOGIN_TITLE);
    }

    private void inizializzaBindingCancella() {
        pulsanteCancella.disableProperty().bind(
                campoUsername.textProperty().isEmpty()
                        .and(campoPassword.textProperty().isEmpty())
                        .and(campoNuovaPassword.textProperty().isEmpty()));
    }

    @FXML
    private void cancellaTutto() {
        campoUsername.clear();
        campoPassword.clear();
        campoPasswordVisibile.clear();
        campoNuovaPassword.clear();
        campoNuovaPasswordVisibile.clear();
        resetErrori();
    }

    @FXML
    private void tornaIndietro() {
        SceneRouter.tornaIndietro();
    }

    private void configuraNavigazioneEnter() {
        configuraNavigazioneUsername();
        configuraNavigazionePassword();
        configuraNavigazioneNuovaPassword();
    }

    private void configuraNavigazioneUsername() {
        campoUsername.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                focusPasswordField();
                event.consume();
            }
        });
    }

    private void configuraNavigazionePassword() {
        javafx.event.EventHandler<javafx.scene.input.KeyEvent> goToNuova = event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                focusNuovaPasswordField();
                event.consume();
            }
        };
        campoPassword.setOnKeyPressed(goToNuova);
        campoPasswordVisibile.setOnKeyPressed(goToNuova);
    }

    private void configuraNavigazioneNuovaPassword() {
        javafx.event.EventHandler<javafx.scene.input.KeyEvent> doConfirm = event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                if (!pulsanteConferma.isDisable())
                    gestisciConferma();
                event.consume();
            }
        };
        campoNuovaPassword.setOnKeyPressed(doConfirm);
        campoNuovaPasswordVisibile.setOnKeyPressed(doConfirm);
    }

    private void focusPasswordField() {
        if (passwordVisibile) {
            campoPasswordVisibile.requestFocus();
        } else {
            campoPassword.requestFocus();
        }
    }

    private void focusNuovaPasswordField() {
        if (nuovaPasswordVisibile) {
            campoNuovaPasswordVisibile.requestFocus();
        } else {
            campoNuovaPassword.requestFocus();
        }
    }

    @FXML
    private void gestisciConferma() {
        resetErrori();

        if (!validaPassword()) {
            return;
        }

        eseguiAggiornamentoPassword();
    }

    private boolean validaPassword() {
        if (!campoNuovaPassword.getText().equals(campoPassword.getText())) {
            mostraErrore(erroreNuovaPassword, "Password non valida");
            applicaStileErrore();
            animaShake();
            return false;
        }
        return true;
    }

    private void applicaStileErrore() {
        setErrorStyle(campoPassword);
        setErrorStyle(campoNuovaPassword);
        setErrorStyle(campoPasswordVisibile);
        setErrorStyle(campoNuovaPasswordVisibile);
    }

    private void eseguiAggiornamentoPassword() {
        LOGGER.info("Recupero password per: " + campoUsername.getText());

        try {
            RecoveryApiService apiService = new RecoveryApiService();
            RecoveryRespond response = apiService.updateApi(campoUsername.getText(), campoNuovaPassword.getText());

            if (response.isSuccess()) {
                gestisciSuccesso(response);
            } else {
                gestisciErroreResponse(response);
            }
        } catch (Exception e) {
            gestisciErroreConnessione();
        }
    }

    private void gestisciSuccesso(RecoveryRespond response) {
        LOGGER.info("Password aggiornata con successo! " + response.getMessage());
        SceneRouter.mostraAlert(javafx.scene.control.Alert.AlertType.INFORMATION,
                "Successo",
                "Password aggiornata",
                "La tua password è stata aggiornata con successo.");
        SceneRouter.cambiaScena(LOGIN_FXML, LOGIN_WIDTH, LOGIN_HEIGHT, LOGIN_TITLE);
    }

    private void gestisciErroreResponse(RecoveryRespond response) {
        LOGGER.info("Errore aggiornamento: " + response.getMessage());
        mostraErrore(erroreNuovaPassword,
                response.getMessage() != null ? response.getMessage() : "Errore durante l'aggiornamento");
        animaShake();
    }

    private void gestisciErroreConnessione() {
        mostraErrore(erroreNuovaPassword, "Errore di connessione al server");
        animaShake();
    }

    private void setErrorStyle(javafx.scene.control.Control control) {
        if (!control.getStyleClass().contains(ERR_MSG)) {
            control.getStyleClass().add(ERR_MSG);
        }
    }

    private void removeErrorStyle(javafx.scene.control.Control control) {
        control.getStyleClass().remove(ERR_MSG);
    }

    private void mostraErrore(Label label, String msg) {
        label.setText(msg);
        label.setVisible(true);
    }

    private void resetErrori() {
        erroreUsername.setVisible(false);
        errorePassword.setVisible(false);
        erroreNuovaPassword.setVisible(false);
        removeErrorStyle(campoPassword);
        removeErrorStyle(campoNuovaPassword);
        removeErrorStyle(campoPasswordVisibile);
        removeErrorStyle(campoNuovaPasswordVisibile);
    }

    private void animaIngresso() {
        FadeTransition ft = new FadeTransition(Duration.millis(800), contenitoreRecovery);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void animaShake() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), boxForm);
        tt.setFromX(0);
        tt.setToX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }
}