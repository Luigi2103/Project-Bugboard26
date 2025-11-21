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

    private static final String COLOR_PRIMARY = "#3498DB";
    private static final String COLOR_PRIMARY_DARK = "#2C3E50";
    private static final String STYLE_PRIMARY_BUTTON = "-fx-background-color: linear-gradient(to right, #F5F0D9 0%, " + COLOR_PRIMARY + " 100%); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 10; -fx-padding: 12 40; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(52, 152, 219, 0.4), 10, 0, 0, 3);";
    private static final String STYLE_TOGGLE_USER = "-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 16; -fx-padding: 8 20; -fx-cursor: hand;";
    private static final String STYLE_TOGGLE_ADMIN = "-fx-background-color: " + COLOR_PRIMARY_DARK + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 16; -fx-padding: 8 20; -fx-cursor: hand;";

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

        setupHoverEffects();

        usernameField.prefWidthProperty().bind(formBox.widthProperty());
        passwordField.prefWidthProperty().bind(formBox.widthProperty());
        loginButton.prefWidthProperty().bind(formBox.widthProperty());
        modeBox.prefWidthProperty().bind(formBox.widthProperty());
        loginButton.setStyle(STYLE_PRIMARY_BUTTON);

        modeGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            currentMode = (newT == adminRadio) ? UserMode.ADMIN : UserMode.USER;
            updateUserTypeUI();
            usernameField.clear();
            passwordField.clear();
            applyModeStyles();
        });
        applyModeStyles();
    }

    private void setupHoverEffects() {
        // Hover effect per il pulsante di login
        loginButton.setOnMouseEntered(e -> {
            loginButton.setStyle(STYLE_PRIMARY_BUTTON + " -fx-scale-x: 1.03; -fx-scale-y: 1.03;");
        });

        loginButton.setOnMouseExited(e -> {
            loginButton.setStyle(STYLE_PRIMARY_BUTTON);
        });

        userRadio.setOnMouseEntered(e -> userRadio.setStyle(STYLE_TOGGLE_USER + " -fx-scale-x: 1.03; -fx-scale-y: 1.03;"));
        userRadio.setOnMouseExited(e -> applyModeStyles());
        adminRadio.setOnMouseEntered(e -> adminRadio.setStyle(STYLE_TOGGLE_ADMIN + " -fx-scale-x: 1.03; -fx-scale-y: 1.03;"));
        adminRadio.setOnMouseExited(e -> applyModeStyles());

        // Focus effect per i campi di input
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                usernameField.setStyle("-fx-background-color: #ffffff; " +
                        "-fx-background-radius: 10; -fx-border-color: #3498DB; " +
                        "-fx-border-radius: 10; -fx-border-width: 2; -fx-padding: 12; -fx-font-size: 14px;");
            } else {
                usernameField.setStyle("-fx-background-color: #f8f9fa; " +
                        "-fx-background-radius: 10; -fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 10; -fx-padding: 12; -fx-font-size: 14px;");
            }
        });

        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordField.setStyle("-fx-background-color: #ffffff; " +
                        "-fx-background-radius: 10; -fx-border-color: #3498DB; " +
                        "-fx-border-radius: 10; -fx-border-width: 2; -fx-padding: 12; -fx-font-size: 14px;");
            } else {
                passwordField.setStyle("-fx-background-color: #f8f9fa; " +
                        "-fx-background-radius: 10; -fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 10; -fx-padding: 12; -fx-font-size: 14px;");
            }
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
            userTypeLabel.setTextFill(Color.web(COLOR_PRIMARY_DARK));
            userTypeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + COLOR_PRIMARY_DARK + ";");
            adminRadio.setSelected(true);
        } else {
            userTypeLabel.setText("Modalità Utente");
            userTypeLabel.setTextFill(Color.web(COLOR_PRIMARY));
            userTypeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + COLOR_PRIMARY + ";");
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

    private void applyModeStyles() {
        if (currentMode == UserMode.ADMIN) {
            adminRadio.setStyle(STYLE_TOGGLE_ADMIN);
            userRadio.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_PRIMARY_DARK + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 16; -fx-padding: 8 20; -fx-cursor: hand;");
        } else {
            userRadio.setStyle(STYLE_TOGGLE_USER);
            adminRadio.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 16; -fx-padding: 8 20; -fx-cursor: hand;");
        }
    }
}

