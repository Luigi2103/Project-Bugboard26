package it.unina.bugboard.app;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import it.unina.bugboard.navigation.SceneRouter;

import java.io.IOException;

public class BugBoard extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.getIcons().add(new Image(getClass().getResource("/it/unina/bugboard/foto/logoCompleto.png").toExternalForm()));
        SceneRouter.inizializza(stage);
        SceneRouter.cambiaScena("/it/unina/bugboard/fxml/login.fxml", 900, 850, "BugBoard - Login");
        stage.setResizable(true);
        stage.setMinWidth(360);
        stage.setMinHeight(600);
        stage.centerOnScreen();
        stage.show();
    }
}
