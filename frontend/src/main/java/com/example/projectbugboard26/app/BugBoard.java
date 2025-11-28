package com.example.projectbugboard26.app;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import com.example.projectbugboard26.navigation.SceneRouter;

import java.io.IOException;

public class BugBoard extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        stage.getIcons().add(new Image(getClass().getResource("/com/example/projectbugboard26/foto/logoCompleto.png").toExternalForm()));
        SceneRouter.inizializza(stage);
        // PROVVISORIO: Per testare la schermata di inserimento utente, cambiare a
        // insert_user.fxml
        SceneRouter.cambiaScena("/com/example/projectbugboard26/fxml/login.fxml", 900, 850, "BugBoard - Login");
        stage.setResizable(true);
        stage.setMinWidth(360);
        stage.setMinHeight(600);
        stage.centerOnScreen();
        stage.show();
    }
}
