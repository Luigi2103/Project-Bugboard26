package com.example.projectbugboard26.controller;

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
import com.example.projectbugboard26.SceneRouter;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private ImageView logoImmagine;
    @FXML private TextField campoUsername;
    @FXML private PasswordField campoPassword;
    @FXML private Button pulsanteLogin;

    @FXML private ToggleGroup gruppoModalita;
    @FXML private ToggleButton toggleUtente;
    @FXML private ToggleButton toggleAdmin;

    @FXML private HBox boxModalita;
    @FXML private Label etichettaModalita;
    @FXML private VBox contenitoreLogin;
    @FXML private VBox boxForm;

    private enum ModalitaUtente { UTENTE, ADMIN }
    private ModalitaUtente modalitaCorrente = ModalitaUtente.UTENTE;

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
    }

    // --------------------------------------------------------
    // SETUP UI
    // --------------------------------------------------------

    private void caricaLogo() {
        try {
            Image logo = new Image(
                    getClass().getResource("/com/example/projectbugboard26/foto/logoCompleto.png")
                            .toExternalForm()
            );
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
    }

    // --------------------------------------------------------
    // ANIMAZIONI
    // --------------------------------------------------------

    private void animaFormLogin() {
        fadeIn(contenitoreLogin, 800);
        slideUp(contenitoreLogin, 600);
        fadeIn(logoImmagine, 1000);
    }

    private void fadeIn(javafx.scene.Node nodo, int ms) {
        FadeTransition ft = new FadeTransition(Duration.millis(ms), nodo);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void slideUp(javafx.scene.Node nodo, int ms) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(ms), nodo);
        tt.setFromY(30);
        tt.setToY(0);
        tt.play();
    }

    private void animaErrore() {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), contenitoreLogin);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();

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
    // UI MODALITÀ
    // --------------------------------------------------------

    private void aggiornaUI() {
        boolean isAdmin = modalitaCorrente == ModalitaUtente.ADMIN;

        etichettaModalita.setText(isAdmin ? "Modalità Amministratore" : "Modalità Utente");
        aggiornaStileEtichetta(isAdmin);

        if (isAdmin) toggleAdmin.setSelected(true);
        else toggleUtente.setSelected(true);
    }

    private void aggiornaStileEtichetta(boolean admin) {
        etichettaModalita.getStyleClass().removeAll("admin-label", "user-label");
        etichettaModalita.getStyleClass().add(admin ? "admin-label" : "user-label");
    }

    // --------------------------------------------------------
    // LOGICA LOGIN
    // --------------------------------------------------------

    @FXML
    private void gestisciLogin() {
        if (campoUsername.getText().isEmpty() || campoPassword.getText().isEmpty()) {
            animaErrore();
            return;
        }

        animaClickPulsante();

        System.out.println("Login come " + modalitaCorrente);
        System.out.println("Username: " + campoUsername.getText());

        SceneRouter.switchTo("dashboard.fxml", 1100, 800, "BugBoard - Dashboard");
    }

    private void animaClickPulsante() {
        FadeTransition ft = new FadeTransition(Duration.millis(150), pulsanteLogin);
        ft.setFromValue(1);
        ft.setToValue(0.7);
        ft.setCycleCount(2);
        ft.setAutoReverse(true);
        ft.play();
    }
}
