package it.unina.bugboard.navigation;

import it.unina.bugboard.navigation.exception.SceneLoadException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.application.Platform;
import it.unina.bugboard.app.BugBoard;
import it.unina.bugboard.login.LoginController;
import it.unina.bugboard.login.LoginApiService;
import it.unina.bugboard.inserimentoutente.InsertUserController;
import it.unina.bugboard.inserimentoutente.InsertUserApiService;
import it.unina.bugboard.homepage.HomeApiService;
import it.unina.bugboard.homepage.HomePageController;
import it.unina.bugboard.issuedetails.DettaglioIssueController;
import it.unina.bugboard.issuedetails.IssueApiService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class SceneRouter {
    private static Stage primaryStage;
    private static final it.unina.bugboard.common.SessionManager sessionManager = new it.unina.bugboard.common.SessionManager();
    private static final LoginApiService loginApiService = new LoginApiService();
    private static final InsertUserApiService insertUserApiService = new InsertUserApiService(sessionManager);
    private static final HomeApiService homeApiService = new it.unina.bugboard.homepage.HomeApiService(sessionManager);
    private static final IssueApiService issueApiService = new IssueApiService(sessionManager);
    private static final Map<Class<?>, Callback<Class<?>, Object>> controllerFactories = new HashMap<>();

    // Variabile per passare l'ID dell'issue tra scene
    private static Integer currentIssueId;

    static {
        controllerFactories.put(LoginController.class, param -> new LoginController(loginApiService, sessionManager));
        controllerFactories.put(InsertUserController.class, param -> new InsertUserController(insertUserApiService));
        controllerFactories.put(HomePageController.class,
                param -> new HomePageController(homeApiService, sessionManager));
        controllerFactories.put(DettaglioIssueController.class,
                param -> new DettaglioIssueController(issueApiService, sessionManager));
    }

    private SceneRouter() {
    }

    public static void inizializza(Stage stage) {
        primaryStage = stage;
    }

    public static void cambiaScena(String fxml, double width, double height, String title) {
        try {
            caricaScena(fxml, width, height, title);
        } catch (Exception e) {
            mostraMessaggioErrore(fxml, e);
        }
    }

    // NUOVO METODO
    public static void cambiaScenaConIssue(String fxml, double width, double height, String title, Integer issueId) {
        currentIssueId = issueId;
        cambiaScena(fxml, width, height, title);
    }

    private static void mostraMessaggioErrore(String fxml, Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore caricamento scena");
        alert.setHeaderText(null);
        String msg = "Impossibile caricare " + fxml + "\n" + e.getMessage();
        if (e.getCause() != null) {
            msg += "\nCausa: " + e.getCause().getMessage();
        }
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private static void caricaScena(String fxml, double width, double height, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(BugBoard.class.getResource(fxml));

            loader.setControllerFactory(param -> {
                if (controllerFactories.containsKey(param)) {
                    return controllerFactories.get(param).call(param);
                } else {
                    try {
                        return param.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new SceneLoadException(
                                "Errore durante la creazione del controller: " + param.getName(),
                                e);
                    }
                }
            });

            javafx.scene.Parent root = loader.load();

            // NUOVO: Passa l'ID dell'issue al controller se necessario
            if (currentIssueId != null && loader.getController() instanceof DettaglioIssueController) {
                ((DettaglioIssueController) loader.getController()).setIssueId(currentIssueId);
                currentIssueId = null; // Reset dopo l'uso
            }

            if (primaryStage.getScene() == null) {
                Scene scene = new Scene(root, width, height);
                primaryStage.setTitle(title);
                primaryStage.setScene(scene);
            } else {
                primaryStage.getScene().setRoot(root);
                primaryStage.setTitle(title);

                if (!primaryStage.isMaximized()) {
                    primaryStage.setWidth(width);
                    primaryStage.setHeight(height);
                    primaryStage.centerOnScreen();
                }
            }

        } catch (IOException e) {
            throw new SceneLoadException("Impossibile caricare la scena: " + fxml, e);
        }
    }
}