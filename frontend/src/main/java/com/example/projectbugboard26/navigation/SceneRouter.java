package com.example.projectbugboard26.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.projectbugboard26.BugBoard;

public final class SceneRouter {
    private static Stage primaryStage;

    private SceneRouter() {}

    public static void inizializza(Stage stage) {
        primaryStage = stage;
    }

    public static void cambiaScena(String fxml, double width, double height, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(BugBoard.class.getResource(fxml));
            Scene scene = new Scene(loader.load(), width, height);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Impossibile caricare " + fxml, e);
        }
    }
}