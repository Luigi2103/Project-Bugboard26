package com.example.projectbugboard26;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class SceneRouter {
    private static Stage primaryStage;

    private SceneRouter() {}

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void switchTo(String fxml, double width, double height, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(BugBoard.class.getResource(fxml));
            Scene scene = new Scene(loader.load(), width, height);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
        } catch (Exception e) {
            throw new RuntimeException("Impossibile caricare " + fxml, e);
        }
    }
}