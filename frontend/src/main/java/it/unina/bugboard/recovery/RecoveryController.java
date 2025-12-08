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

    Logger logger = Logger.getLogger(RecoveryController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        caricaLogo();
        inizializzaBindings();
        inizializzaTogglePassword();
        inizializzaBindingCancella();
        animaIngresso();
        gestisciResponsive();
        Platform.runLater(() -> contenitoreRecovery.requestFocus());
    }

    private void gestisciResponsive() {
        if (contenitoreRecovery.getScene() != null) {
            impostaListenerLarghezza(contenitoreRecovery.getScene());
        } else {
            contenitoreRecovery.sceneProperty().addListener((obs, oldScene, newScene) -> {
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

        aggiornaScala(scene);
    }

    private void aggiornaScala(javafx.scene.Scene scene) {
        double width = scene.getWidth();
        double height = scene.getHeight();

        // Logica di resize per il padding (adattata da LoginController)
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

        // Clamp tra 1.0 e 1.3
        scale = Math.max(1.0, Math.min(scale, 1.3));

        contenitoreRecovery.setScaleX(scale);
        contenitoreRecovery.setScaleY(scale);
    }

    private void caricaLogo() {
        try {
            Image logo = new Image(
                    getClass().getResource("/it/unina/bugboard/foto/logoCompleto.png").toExternalForm());
            logoImmagine.setImage(logo);
        } catch (Exception e) {
            logger.info("Errore caricamento logo: " + e.getMessage());
        }
    }

    private void inizializzaBindings() {
        campoUsername.prefWidthProperty().bind(boxForm.widthProperty());
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

    private void aggiornaVisibilitaNuovaPassword(boolean visibile) {

        FontAwesomeIconView icon = new FontAwesomeIconView(
                visibile ? FontAwesomeIcon.EYE_SLASH : FontAwesomeIcon.EYE);
        icon.setSize("18");

        toggleNuovaPassword.setGraphic(icon);
        toggleNuovaPassword.setText(null);

        campoNuovaPassword.setVisible(!visibile);
        campoNuovaPasswordVisibile.setVisible(visibile);

        if (visibile) {
            campoNuovaPasswordVisibile.requestFocus();
            campoNuovaPasswordVisibile.positionCaret(campoNuovaPasswordVisibile.getText().length());
        } else {
            campoNuovaPassword.requestFocus();
            campoNuovaPassword.positionCaret(campoNuovaPassword.getText().length());
        }
    }

    @FXML
    private void gestisciAnnulla() {
        // Torna al login
        SceneRouter.cambiaScena(LOGIN_FXML, LOGIN_WIDTH, LOGIN_HEIGHT, LOGIN_TITLE);
    }

    private void inizializzaBindingCancella() {
        // Cancella Tutto si abilita se almeno un campo è compilato
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
        // Torna al login
        SceneRouter.cambiaScena(LOGIN_FXML, LOGIN_WIDTH, LOGIN_HEIGHT, LOGIN_TITLE);
    }

    @FXML
    private void gestisciConferma() {
        resetErrori();
        boolean valid = true;

        if (campoUsername.getText().isEmpty()) {
            mostraErrore(erroreUsername, "Inserisci username");
            valid = false;
        }
        if (campoPassword.getText().isEmpty()) {
            mostraErrore(errorePassword, "Inserisci password attuale");
            valid = false;
        }
        if (campoNuovaPassword.getText().isEmpty()) {
            mostraErrore(erroreNuovaPassword, "Inserisci nuova password");
            valid = false;
        }

        if (!campoNuovaPassword.getText().equals(campoPassword.getText())) {
            mostraErrore(erroreNuovaPassword, "Le password non coincidono");
            valid = false;
        }

        if (valid) {
            logger.info("Recupero password per: " + campoUsername.getText());
            logger.info("Vecchia: " + campoPassword.getText());
            logger.info("Nuova: " + campoNuovaPassword.getText());

            try {
                RecoveryApiService apiService = new RecoveryApiService();
                RecoveryRespond response = apiService.updateApi(campoUsername.getText(), campoNuovaPassword.getText());

                if (response.isSuccess()) {
                    logger.info("Password aggiornata con successo! " + response.getMessage());
                    SceneRouter.cambiaScena(LOGIN_FXML, LOGIN_WIDTH, LOGIN_HEIGHT, LOGIN_TITLE);
                } else {
                    logger.info("Errore aggiornamento: " + response.getMessage());
                    mostraErrore(erroreNuovaPassword,
                            response.getMessage() != null ? response.getMessage() : "Errore durante l'aggiornamento");
                    animaShake();
                }
            } catch (Exception e) {
                e.printStackTrace();
                mostraErrore(erroreNuovaPassword, "Errore di connessione al server");
                animaShake();
            }

        } else {
            animaShake();
        }
    }

    private void mostraErrore(Label label, String msg) {
        label.setText(msg);
        label.setVisible(true);
    }

    private void resetErrori() {
        erroreUsername.setVisible(false);
        errorePassword.setVisible(false);
        erroreNuovaPassword.setVisible(false);
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
