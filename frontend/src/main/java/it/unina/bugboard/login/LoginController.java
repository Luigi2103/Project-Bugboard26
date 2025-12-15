package it.unina.bugboard.login;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import javafx.application.Platform;
import it.unina.bugboard.navigation.SceneRouter;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.util.logging.Logger;
import java.net.URL;
import java.util.ResourceBundle;
import it.unina.bugboard.common.SessionManager;

public class LoginController implements Initializable {

    @FXML
    private ImageView logoImmagine;
    @FXML
    private TextField campoUsername;
    @FXML
    private PasswordField campoPassword;
    @FXML
    private TextField campoPasswordVisibile;
    @FXML
    private Button togglePassword;
    @FXML
    private Button pulsanteLogin;
    @FXML
    private ToggleGroup gruppoModalita;
    @FXML
    private ToggleButton toggleUtente;
    @FXML
    private ToggleButton toggleAdmin;
    @FXML
    private HBox boxModalita;
    @FXML
    private Label etichettaModalita;
    @FXML
    private Label etichettaUsername;
    @FXML
    private Label etichettaPassword;
    @FXML
    private Label erroreUsername;
    @FXML
    private Label errorePassword;
    @FXML
    private VBox contenitoreLogin;
    @FXML
    private VBox boxForm;
    @FXML
    private Button linkRecuperoPassword;

    private enum ModalitaUtente {
        UTENTE, ADMIN
    }

    private ModalitaUtente modalitaCorrente = ModalitaUtente.UTENTE;
    private TranslateTransition shakeTransition;
    private boolean passwordVisibile = false;
    private final LoginApiService loginApiService;
    private final SessionManager sessionManager;
    private static final String MSG_TXTFIELD_ERROR = "text-field-error";
    private static final Logger log = Logger.getLogger(LoginController.class.getName());

    public LoginController(LoginApiService loginApiService, SessionManager sessionManager) {
        this.loginApiService = loginApiService;
        this.sessionManager = sessionManager;
    }

    // --------------------------------------------------------
    // INIZIALIZZAZIONE
    // --------------------------------------------------------

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        caricaLogo();
        inizializzaBindings();
        inizializzaListenerModalita();
        inizializzaTogglePassword();
        animaFormLogin();
        aggiornaUI();
        gestisciResponsive();
        Platform.runLater(() -> contenitoreLogin.requestFocus());
    }

    // --------------------------------------------------------
    // SETUP UI
    // --------------------------------------------------------

    private void caricaLogo() {
        try {
            Image logo = new Image(
                    getClass().getResource("/it/unina/bugboard/foto/logoCompleto.png").toExternalForm());
            logoImmagine.setImage(logo);
            logoImmagine.setPreserveRatio(true);
            logoImmagine.setFitWidth(200);
        } catch (Exception e) {
            log.info("Errore caricamento logo: " + e.getMessage());
        }
    }

    private void inizializzaBindings() {
        campoUsername.prefWidthProperty().bind(boxForm.widthProperty());
        campoPassword.prefWidthProperty().bind(boxForm.widthProperty());
        pulsanteLogin.prefWidthProperty().bind(boxForm.widthProperty());
        boxModalita.prefWidthProperty().bind(boxForm.widthProperty());

        pulsanteLogin.disableProperty().bind(
                campoUsername.textProperty().isEmpty()
                        .or(campoPassword.textProperty().isEmpty()));
    }

    private void inizializzaListenerModalita() {
        gruppoModalita.selectedToggleProperty().addListener((obs, vecchio, nuovo) -> {
            modalitaCorrente = (nuovo == toggleAdmin) ? ModalitaUtente.ADMIN : ModalitaUtente.UTENTE;
            aggiornaUI();
        });
        campoUsername.setOnAction(e -> campoPassword.requestFocus());
    }

    private void inizializzaTogglePassword() {
        campoPasswordVisibile.textProperty().bindBidirectional(campoPassword.textProperty());
        aggiornaVisibilitaPassword(false);
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

    // --------------------------------------------------------
    // ANIMAZIONI
    // --------------------------------------------------------

    private void animaFormLogin() {
        dissolvenzaInEntrata(contenitoreLogin, 800);
        scorriSu(contenitoreLogin, 600);
        dissolvenzaInEntrata(logoImmagine, 1000);
    }

    private void dissolvenzaInEntrata(javafx.scene.Node nodo, int ms) {
        FadeTransition ft = new FadeTransition(Duration.millis(ms), nodo);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void scorriSu(javafx.scene.Node nodo, int ms) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(ms), nodo);
        tt.setFromY(30);
        tt.setToY(0);
        tt.play();
    }

    private void animaErrore() {

        if (shakeTransition != null) {
            shakeTransition.stop();
        }

        contenitoreLogin.setTranslateX(0);
        shakeTransition = new TranslateTransition(Duration.millis(50), contenitoreLogin);
        shakeTransition.setFromX(0);
        shakeTransition.setToX(10);
        shakeTransition.setCycleCount(6);
        shakeTransition.setAutoReverse(true);
        shakeTransition.setOnFinished(e -> contenitoreLogin.setTranslateX(0));
        shakeTransition.play();
        lampeggia(pulsanteLogin, 200);
    }

    private void lampeggia(Button btn, int ms) {
        FadeTransition ft = new FadeTransition(Duration.millis(ms), btn);
        ft.setFromValue(1);
        ft.setToValue(0.5);
        ft.setCycleCount(2);
        ft.setAutoReverse(true);
        ft.play();
    }

    // --------------------------------------------------------
    // RESPONSIVE
    // --------------------------------------------------------

    private void gestisciResponsive() {
        if (contenitoreLogin.getScene() != null) {
            impostaListenerLarghezza(contenitoreLogin.getScene());
        } else {
            contenitoreLogin.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    impostaListenerLarghezza(newScene);
                }
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
            contenitoreLogin.setPadding(new javafx.geometry.Insets(30, 20, 30, 20));
        } else {
            contenitoreLogin.setPadding(new javafx.geometry.Insets(30, 50, 30, 50));
        }

        double baseWidth = 1000.0;
        double baseHeight = 800.0;
        double scaleX = width / baseWidth;
        double scaleY = height / baseHeight;
        double scale = Math.clamp(1.0, Math.min(scaleX, scaleY), 1.3);

        contenitoreLogin.setScaleX(scale);
        contenitoreLogin.setScaleY(scale);
    }

    // --------------------------------------------------------
    // UI MODALITÀ
    // --------------------------------------------------------

    private void aggiornaUI() {
        boolean isAdmin = modalitaCorrente == ModalitaUtente.ADMIN;

        etichettaModalita.setText(isAdmin ? "Modalità Amministratore" : "Modalità Utente");

        if (isAdmin)
            toggleAdmin.setSelected(true);
        else
            toggleUtente.setSelected(true);
    }

    // --------------------------------------------------------
    // LOGICA LOGIN
    // --------------------------------------------------------

    @FXML
    private void gestisciLogin() {
        resetErrorStyles();

        animaClickPulsante();

        String username = campoUsername.getText();
        String password = campoPassword.getText();
        boolean isAdmin = gruppoModalita.getSelectedToggle() == toggleAdmin;

        new Thread(() -> inviaRichiestaLogin(username, password, isAdmin)).start();
    }

    private void inviaRichiestaLogin(String username, String password, boolean isAdmin) {
        try {
            RispostaLogin risposta = loginApiService.login(username, password,
                    isAdmin);
            gestisciRisultatoLogin(risposta);
        } catch (Exception e) {
            Platform.runLater(() -> {
                mostraErroreInline(errorePassword, "Errore di connessione al server: " + e.getMessage());
                animaErrore();
            });
        }
    }

    private void gestisciRisultatoLogin(RispostaLogin risposta) {
        Platform.runLater(() -> {
            if (risposta.isSuccess()) {
                this.sessionManager.setToken(risposta.getToken());
                boolean isAdmin = this.sessionManager.isAdmin();

                log.info("Login effettuato con successo. ID Utente: " + risposta.getIdUtente() +
                        ", Username: " + this.sessionManager.getUsername() +
                        ", Modaità: " + (isAdmin ? "ADMIN" : "USER"));

                SceneRouter.cambiaScena("/it/unina/bugboard/fxml/home.fxml", 1200, 850,
                        "BugBoard - Home");
            } else {
                setErrorStyle(campoUsername);
                setErrorStyle(campoPassword);
                mostraErroreInline(errorePassword, risposta.getMessage());
                animaErrore();
            }
        });
    }

    @FXML
    private void vaiARecuperoPassword() {
        SceneRouter.cambiaScena("/it/unina/bugboard/fxml/recovery.fxml", 900, 930,
                "BugBoard - Recupero Password");
    }

    private void animaClickPulsante() {
        FadeTransition ft = new FadeTransition(Duration.millis(150), pulsanteLogin);
        ft.setFromValue(1);
        ft.setToValue(0.7);
        ft.setCycleCount(2);
        ft.setAutoReverse(true);
        ft.play();
    }

    private void setErrorStyle(Control control) {
        if (!control.getStyleClass().contains(MSG_TXTFIELD_ERROR)) {
            control.getStyleClass().add(MSG_TXTFIELD_ERROR);
        }
    }

    private void removeErrorStyle(Control control) {
        control.getStyleClass().remove(MSG_TXTFIELD_ERROR);
    }

    private void resetErrorStyles() {
        removeErrorStyle(campoUsername);
        removeErrorStyle(campoPassword);
        erroreUsername.setVisible(false);
        errorePassword.setVisible(false);
    }

    private void mostraErroreInline(Label label, String messaggio) {
        label.setText(messaggio);
        label.setVisible(true);
    }

}
