package com.example.projectbugboard26;

import javafx.application.Application;
import javafx.stage.Stage;
import com.example.projectbugboard26.navigation.SceneRouter;

import java.io.IOException;

public class BugBoard extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SceneRouter.inizializza(stage);
        SceneRouter.cambiaScena("login.fxml", 900, 800, "BugBoard - Login");
        stage.setResizable(true);
        stage.setMinWidth(360);
        stage.setMinHeight(600);
        stage.centerOnScreen();
        stage.show();
    }
}

