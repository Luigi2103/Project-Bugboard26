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
import com.example.projectbugboard26.navigation.SceneRouter;
import com.example.projectbugboard26.exception.CampoUsernameVuotoException;
import com.example.projectbugboard26.exception.CampoPasswordVuotoException;

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
    @FXML private Label etichettaUsername;
    @FXML private Label etichettaPassword;
    @FXML private VBox contenitoreLogin;
    @FXML private VBox boxForm;
    private enum ModalitaUtente { UTENTE, ADMIN }
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
    }

    // --------------------------------------------------------
    // SETUP UI
    // --------------------------------------------------------

    private void caricaLogo() {
        try {
            Image logo = new Image(getClass().getResource("/com/example/projectbugboard26/foto/logoCompleto.png").toExternalForm());
            logoImmagine.setImage(logo);
            logoImmagine.setPreserveRatio(true);
            logoImmagine.setFitWidth(200);
        } catch (Exception e) {
            mostraErrore("Errore caricamento logo" , "Impossibile caricare il logo");
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
        aggiornaStileEtichetta(isAdmin);

        if (isAdmin) toggleAdmin.setSelected(true);
        else toggleUtente.setSelected(true);
    }

    private void aggiornaStileEtichetta(boolean admin) {
        etichettaModalita.getStyleClass().removeAll("admin-label", "user-label");
        etichettaModalita.getStyleClass().add(admin ? "admin-label" : "user-label");
        
        etichettaUsername.getStyleClass().removeAll("admin-field-label", "user-field-label", "field-label");
        etichettaUsername.getStyleClass().add(admin ? "admin-field-label" : "user-field-label");
        
        etichettaPassword.getStyleClass().removeAll("admin-field-label", "user-field-label", "field-label");
        etichettaPassword.getStyleClass().add(admin ? "admin-field-label" : "user-field-label");
    }

    // --------------------------------------------------------
    // LOGICA LOGIN
    // --------------------------------------------------------

    @FXML
    private void gestisciLogin() {
        try {
            controlloCampi();
            animaClickPulsante();

            System.out.println("Login come " + modalitaCorrente);
            System.out.println("Username: " + campoUsername.getText());

            SceneRouter.cambiaScena("dashboard.fxml", 1100, 800, "BugBoard - Dashboard");

        } catch (CampoUsernameVuotoException e) {
            animaErrore();
            mostraErrore("Errore Login", e.getMessage());
        } catch (CampoPasswordVuotoException e) {
            animaErrore();
            mostraErrore("Errore Login", e.getMessage());
        }   
    }


    private void controlloCampi() throws CampoUsernameVuotoException, CampoPasswordVuotoException {
        if (campoUsername.getText().isEmpty()) {
            throw new CampoUsernameVuotoException("Il campo username non può essere vuoto");
        }
        if (campoPassword.getText().isEmpty()) {
            throw new CampoPasswordVuotoException("Il campo password non può essere vuoto");
        }
    }

    private void animaClickPulsante() {
        FadeTransition ft = new FadeTransition(Duration.millis(150), pulsanteLogin);
        ft.setFromValue(1);
        ft.setToValue(0.7);
        ft.setCycleCount(2);
        ft.setAutoReverse(true);
        ft.play();
    }



    private void mostraErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}
