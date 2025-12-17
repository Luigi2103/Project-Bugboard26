package it.unina.bugboard.navigation;

import it.unina.bugboard.navigation.exception.SceneLoadException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Callback;
import it.unina.bugboard.app.BugBoard;
import it.unina.bugboard.login.LoginController;
import it.unina.bugboard.login.LoginApiService;
import it.unina.bugboard.inserimentoutente.InsertUserController;
import it.unina.bugboard.inserimentoutente.InsertUserApiService;
import it.unina.bugboard.homepage.HomeApiService;
import it.unina.bugboard.homepage.HomePageController;
import it.unina.bugboard.issuedetails.DettaglioIssueController;
import it.unina.bugboard.issuedetails.IssueApiService;
import it.unina.bugboard.issues.AllIssuesController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public final class SceneRouter {
    private static Stage primaryStage;
    private static final it.unina.bugboard.common.SessionManager sessionManager = new it.unina.bugboard.common.SessionManager();
    private static final LoginApiService loginApiService = new LoginApiService();
    private static final InsertUserApiService insertUserApiService = new InsertUserApiService(sessionManager);
    private static final HomeApiService homeApiService = new it.unina.bugboard.homepage.HomeApiService(sessionManager);
    private static final IssueApiService issueApiService = new IssueApiService(sessionManager);
    private static final it.unina.bugboard.inserimentoIssue.InsertIssueApiService insertIssueApiService = new it.unina.bugboard.inserimentoIssue.InsertIssueApiService(
            sessionManager);
    private static final Map<Class<?>, Callback<Class<?>, Object>> controllerFactories = new HashMap<>();

    // Stack per la cronologia
    private static final Stack<SceneData> history = new Stack<>();
    private static SceneData currentSceneData;

    // Variabile per passare l'ID dell'issue tra scene
    private static Integer currentIssueId;

    static {
        controllerFactories.put(LoginController.class, param -> new LoginController(loginApiService, sessionManager));
        controllerFactories.put(InsertUserController.class, param -> new InsertUserController(insertUserApiService));
        controllerFactories.put(HomePageController.class,
                param -> new HomePageController(homeApiService, sessionManager));
        controllerFactories.put(DettaglioIssueController.class,
                param -> new DettaglioIssueController(issueApiService, sessionManager));
        controllerFactories.put(it.unina.bugboard.inserimentoIssue.InsertIssueController.class,
                param -> new it.unina.bugboard.inserimentoIssue.InsertIssueController(insertIssueApiService,
                        sessionManager));
        controllerFactories.put(AllIssuesController.class,
                param -> new AllIssuesController(homeApiService, sessionManager));
    }

    private SceneRouter() {
    }

    public static void inizializza(Stage stage) {
        primaryStage = stage;
    }

    public static void cambiaScena(String fxml, double width, double height, String title) {
        eseguiCambioScena(fxml, width, height, title, null);
    }

    // NUOVO METODO
    public static void cambiaScenaConIssue(String fxml, double width, double height, String title, Integer issueId) {
        currentIssueId = issueId; // Set static for immediate use by controller factory/init if needed
        eseguiCambioScena(fxml, width, height, title, issueId);
    }

    private static void eseguiCambioScena(String fxml, double width, double height, String title, Integer issueId) {
        pushHistory();
        currentSceneData = new SceneData(fxml, width, height, title, issueId);
        try {
            caricaScena(fxml, width, height, title);
        } catch (Exception e) {
            mostraMessaggioErrore(fxml, e);
        }
    }

    private static void pushHistory() {
        if (currentSceneData != null) {
            history.push(currentSceneData);
        }
    }

    public static void tornaIndietro() {
        if (history.isEmpty()) {
            System.out.println("Nessuna cronologia disponibile.");
            return;
        }

        SceneData previous = history.pop();
        currentSceneData = previous;
        currentIssueId = previous.issueId;

        try {
            caricaScena(previous.fxml, previous.width, previous.height, previous.title);
        } catch (Exception e) {
            mostraMessaggioErrore(previous.fxml, e);
        }
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

            if (currentIssueId != null && loader.getController() instanceof DettaglioIssueController) {
                ((DettaglioIssueController) loader.getController()).setIssueId(currentIssueId);

            }

            if (primaryStage.getScene() == null) {
                Scene scene = new Scene(root, width, height);
                primaryStage.setTitle(title);
                setupGlobalShortcuts(scene); // Add shortcuts to new scene
                primaryStage.setScene(scene);
            } else {
                primaryStage.getScene().setRoot(root);
                primaryStage.setTitle(title);
                setupGlobalShortcuts(primaryStage.getScene()); // Re-apply or ensure shortcuts on existing scene
            }

        } catch (IOException e) {
            throw new SceneLoadException("Impossibile caricare la scena: " + fxml, e);
        }

    }

    private static void setupGlobalShortcuts(Scene scene) {
        javafx.scene.input.KeyCombination insertIssue = new javafx.scene.input.KeyCodeCombination(
                javafx.scene.input.KeyCode.I, javafx.scene.input.KeyCombination.CONTROL_DOWN);
        javafx.scene.input.KeyCombination registerUser = new javafx.scene.input.KeyCodeCombination(
                javafx.scene.input.KeyCode.U, javafx.scene.input.KeyCombination.CONTROL_DOWN);
        javafx.scene.input.KeyCombination logout = new javafx.scene.input.KeyCodeCombination(
                javafx.scene.input.KeyCode.L, javafx.scene.input.KeyCombination.CONTROL_DOWN);

        // BACK SHORTCUT
        javafx.scene.input.KeyCombination back = new javafx.scene.input.KeyCodeCombination(
                javafx.scene.input.KeyCode.ESCAPE);

        // RECOVERY SHORTCUT
        javafx.scene.input.KeyCombination recovery = new javafx.scene.input.KeyCodeCombination(
                javafx.scene.input.KeyCode.P, javafx.scene.input.KeyCombination.CONTROL_DOWN);

        scene.setOnKeyPressed(event -> {
            if (insertIssue.match(event)) {
                if (sessionManager.isLoggedIn()) {
                    cambiaScena("/it/unina/bugboard/fxml/insert_issue.fxml", 600, 650, "Inserisci Nuova Issue");
                }
            } else if (recovery.match(event)) {
                if (!sessionManager.isLoggedIn()) {
                    cambiaScena("/it/unina/bugboard/fxml/recovery.fxml", 600, 700, "BugBoard - Recupero Password");
                }
            } else if (registerUser.match(event)) {
                if (sessionManager.isLoggedIn() && sessionManager.isAdmin()) {
                    cambiaScena("/it/unina/bugboard/fxml/insert_user.fxml", 450, 600, "Registra Nuovo Utente");
                }
            } else if (logout.match(event)) {
                if (sessionManager.isLoggedIn()) {
                    sessionManager.logout();
                    history.clear(); // Clear history logic
                    currentSceneData = null;
                    cambiaScena("/it/unina/bugboard/fxml/login.fxml", 400, 500, "BugBoard - Login");
                }
            } else if (back.match(event)) {
                tornaIndietro();
            }
        });
    }

    private static class SceneData {
        String fxml;
        double width;
        double height;
        String title;
        Integer issueId;

        public SceneData(String fxml, double width, double height, String title, Integer issueId) {
            this.fxml = fxml;
            this.width = width;
            this.height = height;
            this.title = title;
            this.issueId = issueId;
        }
    }
}