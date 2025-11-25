package com.example.projectbugboard26.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import com.example.projectbugboard26.app.BugBoard;

import java.io.IOException;

public final class SceneRouter {
    private static Stage primaryStage;

    private SceneRouter() {}

    public static void inizializza(Stage stage) {
        primaryStage = stage;
    }

    public static void cambiaScena(String fxml, double width, double height, String title) {
        try {
            CaricaScena(fxml, width, height, title);
        } catch (Exception e) {
            mostraMessaggioErrore(fxml, e);
        }
    }

    private static void mostraMessaggioErrore(String fxml, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore caricamento scena");
        alert.setHeaderText(null);
        alert.setContentText("Impossibile caricare " + fxml + e.getMessage());
        alert.showAndWait();
    }

    private static void CaricaScena(String fxml, double width, double height, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(BugBoard.class.getResource(fxml));
        Scene scene = new Scene(loader.load(), width, height);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
    }
}