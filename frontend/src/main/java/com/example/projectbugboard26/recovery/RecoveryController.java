package com.example.projectbugboard26.recovery;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.application.Platform;
import com.example.projectbugboard26.navigation.SceneRouter;

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
    private Button pulsanteAnnulla;
    @FXML
    private Button pulsanteConferma;

    private boolean passwordVisibile = false;
    private boolean nuovaPasswordVisibile = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        caricaLogo();
        inizializzaBindings();
        inizializzaTogglePassword();
        animaIngresso();
        Platform.runLater(() -> contenitoreRecovery.requestFocus());
    }

    private void caricaLogo() {
        try {
            Image logo = new Image(
                    getClass().getResource("/com/example/projectbugboard26/foto/logoCompleto.png").toExternalForm());
            logoImmagine.setImage(logo);
        } catch (Exception e) {
            System.err.println("Errore caricamento logo: " + e.getMessage());
        }
    }

    private void inizializzaBindings() {
        // Bindings per responsive width se necessario, simile al login
        campoUsername.prefWidthProperty().bind(boxForm.widthProperty());
        // ... altri binding
    }

    private void inizializzaTogglePassword() {
        // Sincronizza testo tra i campi
        campoPasswordVisibile.textProperty().bindBidirectional(campoPassword.textProperty());
        campoNuovaPasswordVisibile.textProperty().bindBidirectional(campoNuovaPassword.textProperty());

        // Gestione visibilità iniziale
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
        campoPassword.setVisible(!visibile);
        campoPasswordVisibile.setVisible(visibile);
        // Aggiorna icona/testo bottone
        togglePassword.setText(visibile ? "👁" : "👁‍🗨"); // Placeholder icons
        togglePassword.getStyleClass().removeAll("icon-visible", "icon-hidden");
        togglePassword.getStyleClass().add(visibile ? "icon-visible" : "icon-hidden");
    }

    private void aggiornaVisibilitaNuovaPassword(boolean visibile) {
        campoNuovaPassword.setVisible(!visibile);
        campoNuovaPasswordVisibile.setVisible(visibile);
        toggleNuovaPassword.setText(visibile ? "👁" : "👁‍🗨");
        toggleNuovaPassword.getStyleClass().removeAll("icon-visible", "icon-hidden");
        toggleNuovaPassword.getStyleClass().add(visibile ? "icon-visible" : "icon-hidden");
    }

    @FXML
    private void gestisciAnnulla() {
        // Torna al login
        SceneRouter.cambiaScena("/com/example/projectbugboard26/fxml/login.fxml", 900, 930, "BugBoard - Login");
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

        if (valid) {
            System.out.println("Recupero password per: " + campoUsername.getText());
            System.out.println("Vecchia: " + campoPassword.getText());
            System.out.println("Nuova: " + campoNuovaPassword.getText());
            // Logica di cambio password qui

            // Torna al login dopo successo (simulato)
            SceneRouter.cambiaScena("/com/example/projectbugboard26/fxml/login.fxml", 900, 930, "BugBoard - Login");
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
