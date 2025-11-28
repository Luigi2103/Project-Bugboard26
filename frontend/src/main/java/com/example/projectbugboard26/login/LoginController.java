package com.example.projectbugboard26.login;

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
import com.example.projectbugboard26.navigation.SceneRouter;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private ImageView logoImmagine;
    @FXML
    private TextField campoUsername;
    @FXML
    private PasswordField campoPassword;
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

    // --------------------------------------------------------
    // INIZIALIZZAZIONE
    // --------------------------------------------------------

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        caricaLogo();
        inizializzaBindings();
        inizializzaListenerModalita();
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
                    getClass().getResource("/com/example/projectbugboard26/foto/logoCompleto.png").toExternalForm());
            logoImmagine.setImage(logo);
            logoImmagine.setPreserveRatio(true);
            logoImmagine.setFitWidth(200);
        } catch (Exception e) {
            System.err.println("Errore caricamento logo: " + e.getMessage());
        }
    }

    private void inizializzaBindings() {
        campoUsername.prefWidthProperty().bind(boxForm.widthProperty());
        campoPassword.prefWidthProperty().bind(boxForm.widthProperty());
        pulsanteLogin.prefWidthProperty().bind(boxForm.widthProperty());
        boxModalita.prefWidthProperty().bind(boxForm.widthProperty());
    }

    private void inizializzaListenerModalita() {
        gruppoModalita.selectedToggleProperty().addListener((obs, vecchio, nuovo) -> {
            modalitaCorrente = (nuovo == toggleAdmin) ? ModalitaUtente.ADMIN : ModalitaUtente.UTENTE;
            aggiornaUI();
            pulisciCampi();
        });
    }

    private void pulisciCampi() {
        campoUsername.clear();
        campoPassword.clear();
        resetErrorStyles();
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
        scene.widthProperty().addListener((obs, oldW, newW) -> {
            double width = newW.doubleValue();
            if (width < 600) {
                contenitoreLogin.setPadding(new javafx.geometry.Insets(30, 20, 30, 20));
            } else {
                contenitoreLogin.setPadding(new javafx.geometry.Insets(30, 50, 30, 50));
            }
        });

        // Initial check
        if (scene.getWidth() < 600) {
            contenitoreLogin.setPadding(new javafx.geometry.Insets(30, 20, 30, 20));
        }
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
        boolean isValid = true;

        if (campoUsername.getText().isEmpty()) {
            setErrorStyle(campoUsername);
            mostraErroreInline(erroreUsername, "Il campo username non può essere vuoto");
            isValid = false;
        }

        if (campoPassword.getText().isEmpty()) {
            setErrorStyle(campoPassword);
            mostraErroreInline(errorePassword, "Il campo password non può essere vuoto");
            isValid = false;
        }

        if (!isValid) {
            animaErrore();
            return;
        }

        animaClickPulsante();

        System.out.println("Login come " + modalitaCorrente);
        System.out.println("Username: " + campoUsername.getText());

        // Naviga alla schermata di inserimento utente
        SceneRouter.cambiaScena("/com/example/projectbugboard26/fxml/insert_user.fxml", 900, 930,
                "BugBoard - Registra Utente");
    }

    @FXML
    private void vaiARecuperoPassword() {
        SceneRouter.cambiaScena("/com/example/projectbugboard26/fxml/recovery.fxml", 900, 930,
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
        if (!control.getStyleClass().contains("text-field-error")) {
            control.getStyleClass().add("text-field-error");
        }
    }

    private void removeErrorStyle(Control control) {
        control.getStyleClass().remove("text-field-error");
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
