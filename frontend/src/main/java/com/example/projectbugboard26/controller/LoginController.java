package com.example.projectbugboard26.controller;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import com.example.projectbugboard26.SceneRouter;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private ImageView logoImageView;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private ToggleGroup modeGroup;
    @FXML
    private RadioButton userRadio;
    @FXML
    private RadioButton adminRadio;

    @FXML
    private Label userTypeLabel;

    @FXML
    private VBox loginContainer;

    @FXML
    private VBox formBox;
    @FXML
    private HBox modeBox;

    private enum UserMode { USER, ADMIN }
    private UserMode currentMode = UserMode.USER;

    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Carica il logo
        try {
            Image logo = new Image(getClass().getResource("/com/example/projectbugboard26/foto/logoCompleto.png").toExternalForm());
            logoImageView.setImage(logo);
            logoImageView.setPreserveRatio(true);
            logoImageView.setFitWidth(200);
        } catch (Exception e) {
            System.err.println("Errore nel caricamento del logo: " + e.getMessage());
        }

        animateLoginForm();

        updateUserTypeUI();

        usernameField.prefWidthProperty().bind(formBox.widthProperty());
        passwordField.prefWidthProperty().bind(formBox.widthProperty());
        loginButton.prefWidthProperty().bind(formBox.widthProperty());
        modeBox.prefWidthProperty().bind(formBox.widthProperty());

        modeGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            currentMode = (newT == adminRadio) ? UserMode.ADMIN : UserMode.USER;
            updateUserTypeUI();
            usernameField.clear();
            passwordField.clear();
        });
    }

    private void animateLoginForm() {
        // Fade in per il container
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), loginContainer);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Slide up per il container
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(600), loginContainer);
        slideUp.setFromY(30);
        slideUp.setToY(0);

        fadeIn.play();
        slideUp.play();

        // Animazione per il logo
        FadeTransition logoFade = new FadeTransition(Duration.millis(1000), logoImageView);
        logoFade.setFromValue(0.0);
        logoFade.setToValue(1.0);
        logoFade.play();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            animateError();
            return;
        }

        // Animazione del pulsante al click
        FadeTransition buttonFade = new FadeTransition(Duration.millis(150), loginButton);
        buttonFade.setFromValue(1.0);
        buttonFade.setToValue(0.7);
        buttonFade.setAutoReverse(true);
        buttonFade.setCycleCount(2);
        buttonFade.play();

        // TODO: Implementare la logica di autenticazione
        System.out.println("Login come " + (currentMode == UserMode.ADMIN ? "Admin" : "Utente"));
        System.out.println("Username: " + username);

        SceneRouter.switchTo("dashboard.fxml", 1100, 800, "BugBoard - Dashboard");
    }

    

    private void updateUserTypeUI() {
        if (currentMode == UserMode.ADMIN) {
            userTypeLabel.setText("Modalità Admin");
            userTypeLabel.getStyleClass().remove("user-label");
            if (!userTypeLabel.getStyleClass().contains("admin-label")) {
                userTypeLabel.getStyleClass().add("admin-label");
            }
            adminRadio.setSelected(true);
        } else {
            userTypeLabel.setText("Modalità Utente");
            userTypeLabel.getStyleClass().remove("admin-label");
            if (!userTypeLabel.getStyleClass().contains("user-label")) {
                userTypeLabel.getStyleClass().add("user-label");
            }
            userRadio.setSelected(true);
        }
    }

    private void animateError() {
        // Animazione di shake per i campi vuoti
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), loginContainer);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();

        // Highlight rosso temporaneo
        FadeTransition errorFade = new FadeTransition(Duration.millis(200), loginButton);
        errorFade.setFromValue(1.0);
        errorFade.setToValue(0.5);
        errorFade.setAutoReverse(true);
        errorFade.setCycleCount(2);
        errorFade.play();
    }

    
}

