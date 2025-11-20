package com.example.projectbugboard26.controller;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

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
    private Button switchButton;
    
    @FXML
    private Label userTypeLabel;
    
    @FXML
    private VBox loginContainer;
    
    private boolean isAdminMode = false;
    
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
        
        // Animazione iniziale
        animateLoginForm();
        
        // Stile iniziale
        updateUserTypeUI();
        
        // Aggiungi effetti hover
        setupHoverEffects();
    }
    
    private void setupHoverEffects() {
        // Hover effect per il pulsante di login
        loginButton.setOnMouseEntered(e -> {
            loginButton.setStyle(loginButton.getStyle() + 
                "-fx-background-color: linear-gradient(to right, #764ba2 0%, #667eea 100%); " +
                "-fx-scale-x: 1.05; -fx-scale-y: 1.05;");
        });
        
        loginButton.setOnMouseExited(e -> {
            loginButton.setStyle("-fx-background-color: linear-gradient(to right, #667eea 0%, #764ba2 100%); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; " +
                "-fx-background-radius: 10; -fx-padding: 12 40; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(102, 126, 234, 0.4), 10, 0, 0, 3);");
        });
        
        // Hover effect per il pulsante switch
        switchButton.setOnMouseEntered(e -> {
            String baseColor = isAdminMode ? "#C0392B" : "#2980B9";
            switchButton.setStyle("-fx-background-color: " + baseColor + "; " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; " +
                "-fx-background-radius: 8; -fx-padding: 8 20; -fx-cursor: hand; " +
                "-fx-scale-x: 1.05; -fx-scale-y: 1.05;");
        });
        
        switchButton.setOnMouseExited(e -> {
            updateUserTypeUI();
        });
        
        // Focus effect per i campi di input
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                usernameField.setStyle("-fx-background-color: #ffffff; " +
                    "-fx-background-radius: 10; -fx-border-color: #667eea; " +
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
                    "-fx-background-radius: 10; -fx-border-color: #667eea; " +
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
        System.out.println("Login come " + (isAdminMode ? "Admin" : "Utente"));
        System.out.println("Username: " + username);
        
        // Qui andrà la logica per navigare alla schermata principale
    }
    
    @FXML
    private void handleSwitchUserType() {
        isAdminMode = !isAdminMode;
        
        // Animazione fade per il label
        FadeTransition labelFade = new FadeTransition(Duration.millis(200), userTypeLabel);
        labelFade.setFromValue(1.0);
        labelFade.setToValue(0.3);
        labelFade.setAutoReverse(true);
        labelFade.setCycleCount(2);
        labelFade.setOnFinished(e -> updateUserTypeUI());
        labelFade.play();
        
        // Animazione del toggle button
        FadeTransition buttonFade = new FadeTransition(Duration.millis(200), switchButton);
        buttonFade.setFromValue(1.0);
        buttonFade.setToValue(0.7);
        buttonFade.setAutoReverse(true);
        buttonFade.setCycleCount(2);
        buttonFade.play();
        
        // Reset dei campi con animazione
        FadeTransition fieldFade = new FadeTransition(Duration.millis(150), usernameField);
        fieldFade.setFromValue(1.0);
        fieldFade.setToValue(0.5);
        fieldFade.setAutoReverse(true);
        fieldFade.setCycleCount(2);
        fieldFade.setOnFinished(e -> {
            usernameField.clear();
            passwordField.clear();
        });
        fieldFade.play();
    }
    
    private void updateUserTypeUI() {
        if (isAdminMode) {
            userTypeLabel.setText("Modalità Admin");
            userTypeLabel.setTextFill(Color.web("#E74C3C"));
            switchButton.setText("Passa a Utente");
            switchButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 8; " +
                "-fx-padding: 8 20; -fx-cursor: hand;");
        } else {
            userTypeLabel.setText("Modalità Utente");
            userTypeLabel.setTextFill(Color.web("#3498DB"));
            switchButton.setText("Passa ad Admin");
            switchButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 8; " +
                "-fx-padding: 8 20; -fx-cursor: hand;");
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

