package com.example.projectbugboard26.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Callback;
import com.example.projectbugboard26.app.BugBoard;
import com.example.projectbugboard26.login.LoginController;
import com.example.projectbugboard26.login.LoginApiService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class SceneRouter {
    private static Stage primaryStage;
    private static final LoginApiService loginApiService = new LoginApiService();
    private static final Map<Class<?>, Callback<Class<?>, Object>> controllerFactories = new HashMap<>();

    static {
        controllerFactories.put(LoginController.class, param -> new LoginController(loginApiService));
    }

    private SceneRouter() {
    }

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
        alert.setContentText("Impossibile caricare " + fxml + " " + e.getMessage());
        alert.showAndWait();
    }

    private static void CaricaScena(String fxml, double width, double height, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(BugBoard.class.getResource(fxml));

        loader.setControllerFactory(param -> {
            if (controllerFactories.containsKey(param)) {
                return controllerFactories.get(param).call(param);
            } else {
                try {
                    return param.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Scene scene = new Scene(loader.load(), width, height);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
    }
}