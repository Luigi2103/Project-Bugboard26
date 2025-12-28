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
import it.unina.bugboard.issues.MyIssuesController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.logging.Logger;

public final class SceneRouter {
    private static final Logger LOGGER = Logger.getLogger(SceneRouter.class.getName());
    private static Stage primaryStage;
    private static final it.unina.bugboard.common.SessionManager sessionManager = new it.unina.bugboard.common.SessionManager();
    private static final LoginApiService loginApiService = new LoginApiService();
    private static final InsertUserApiService insertUserApiService = new InsertUserApiService(sessionManager);
    private static final HomeApiService homeApiService = new it.unina.bugboard.homepage.HomeApiService(sessionManager);
    private static final IssueApiService issueApiService = new IssueApiService(sessionManager);
    private static final it.unina.bugboard.inserimentoissue.InsertIssueApiService insertIssueApiService = new it.unina.bugboard.inserimentoissue.InsertIssueApiService(
            sessionManager);
    private static final Map<Class<?>, Callback<Class<?>, Object>> controllerFactories = new HashMap<>();

    private static final Deque<SceneData> history = new ArrayDeque<>();
    private static SceneData currentSceneData;

    private static Integer currentIssueId;
    private static boolean currentEditMode = false;

    static {
        controllerFactories.put(LoginController.class, param -> new LoginController(loginApiService, sessionManager));
        controllerFactories.put(InsertUserController.class, param -> new InsertUserController(insertUserApiService));
        controllerFactories.put(HomePageController.class,
                param -> new HomePageController(homeApiService, sessionManager));
        controllerFactories.put(DettaglioIssueController.class,
                param -> new DettaglioIssueController(issueApiService, sessionManager));
        controllerFactories.put(it.unina.bugboard.inserimentoissue.InsertIssueController.class,
                param -> new it.unina.bugboard.inserimentoissue.InsertIssueController(insertIssueApiService,
                        sessionManager));
        controllerFactories.put(AllIssuesController.class,
                param -> new AllIssuesController(homeApiService, sessionManager));
        controllerFactories.put(MyIssuesController.class,
                param -> new MyIssuesController(homeApiService, sessionManager));
    }

    private SceneRouter() {
    }

    public static void inizializza(Stage stage) {
        primaryStage = stage;
    }

    public static void cambiaScena(String fxml, double width, double height, String title) {
        eseguiCambioScena(fxml, width, height, title, null, false);
    }

    public static void cambiaScenaConIssue(String fxml, double width, double height, String title, Integer issueId) {
        currentIssueId = issueId;
        eseguiCambioScena(fxml, width, height, title, issueId, false);
    }

    public static void cambiaScenaModificaIssue(String fxml, double width, double height, String title,
            Integer issueId) {
        currentIssueId = issueId;
        eseguiCambioScena(fxml, width, height, title, issueId, true);
    }

    private static void eseguiCambioScena(String fxml, double width, double height, String title, Integer issueId,
            boolean editMode) {
        pushHistory();
        currentSceneData = new SceneData(fxml, width, height, title, issueId);
        currentEditMode = editMode;
        try {
            caricaScena(fxml, width, height, title);
        } catch (Exception e) {
            mostraMessaggioErrore(fxml, e);
        }
    }

    private static void pushHistory() {
        if (currentSceneData != null)
            history.push(currentSceneData);
    }

    public static void tornaIndietro() {
        if (history.isEmpty()) {
            LOGGER.info("Nessuna cronologia disponibile.");
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

    public static void mostraAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        if (primaryStage != null && primaryStage.isShowing())
            alert.initOwner(primaryStage);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static void mostraMessaggioErrore(String fxml, Exception e) {
        String msg = "Impossibile caricare " + fxml + "\n" + e.getMessage();
        if (e.getCause() != null)
            msg += "\nCausa: " + e.getCause().getMessage();
        mostraAlert(Alert.AlertType.ERROR, "Errore caricamento scena", null, msg);
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

            if (currentIssueId != null && loader.getController() instanceof DettaglioIssueController controller) {
                controller.setIssueId(currentIssueId);
                controller.setModificaMode(currentEditMode);
            }

            currentEditMode = false;

            if (primaryStage.getScene() == null) {
                Scene scene = new Scene(root, width, height);
                primaryStage.setTitle(title);
                setupGlobalShortcuts(scene);
                primaryStage.setScene(scene);
            } else {
                primaryStage.getScene().setRoot(root);
                primaryStage.setTitle(title);
                setupGlobalShortcuts(primaryStage.getScene());

                if (!primaryStage.isFullScreen() && !primaryStage.isMaximized()) {
                    primaryStage.setWidth(width);
                    primaryStage.setHeight(height);
                }
            }

        } catch (IOException e) {
            throw new SceneLoadException("Impossibile caricare la scena: " + fxml, e);
        }
    }

    private static void setupGlobalShortcuts(Scene scene) {
        javafx.scene.input.KeyCombination insertIssue = createKeyCombination(javafx.scene.input.KeyCode.I);
        javafx.scene.input.KeyCombination registerUser = createKeyCombination(javafx.scene.input.KeyCode.U);
        javafx.scene.input.KeyCombination logout = createKeyCombination(javafx.scene.input.KeyCode.L);
        javafx.scene.input.KeyCombination ctrlP = createKeyCombination(javafx.scene.input.KeyCode.P);
        javafx.scene.input.KeyCombination ctrlA = createKeyCombination(javafx.scene.input.KeyCode.A);
        javafx.scene.input.KeyCombination ctrlM = createKeyCombination(javafx.scene.input.KeyCode.M);
        javafx.scene.input.KeyCombination back = new javafx.scene.input.KeyCodeCombination(
                javafx.scene.input.KeyCode.ESCAPE);

        scene.setOnKeyPressed(event -> {
            if (insertIssue.match(event)) {
                handleInsertIssue();
            } else if (ctrlP.match(event)) {
                handleRecovery();
            } else if (ctrlA.match(event)) {
                handleAllIssues();
            } else if (registerUser.match(event)) {
                handleRegisterUser();
            } else if (logout.match(event)) {
                handleLogout();
            } else if (ctrlM.match(event)) {
                handleMyIssues();
            } else if (back.match(event)) {
                tornaIndietro();
            }
        });
    }

    private static javafx.scene.input.KeyCombination createKeyCombination(javafx.scene.input.KeyCode keyCode) {
        return new javafx.scene.input.KeyCodeCombination(keyCode, javafx.scene.input.KeyCombination.CONTROL_DOWN);
    }

    private static void handleInsertIssue() {
        if (sessionManager.isLoggedIn())
            cambiaScena("/it/unina/bugboard/fxml/insert_issue.fxml", 600, 650, "Inserisci Nuova Issue");
    }

    private static void handleRecovery() {
        if (!sessionManager.isLoggedIn())
            cambiaScena("/it/unina/bugboard/fxml/recovery.fxml", 600, 700, "BugBoard - Recupero Password");
    }

    private static void handleAllIssues() {
        if (sessionManager.isLoggedIn())
            cambiaScena("/it/unina/bugboard/fxml/all_issues.fxml", 1200, 800, "BugBoard - Tutte le Issue del Progetto");
    }

    private static void handleMyIssues() {
        if (sessionManager.isLoggedIn())
            cambiaScena("/it/unina/bugboard/fxml/my_issues.fxml", 1200, 800, "BugBoard - Le mie Issue");
    }

    private static void handleRegisterUser() {
        if (sessionManager.isLoggedIn() && sessionManager.isAdmin())
            cambiaScena("/it/unina/bugboard/fxml/insert_user.fxml", 450, 600, "Registra Nuovo Utente");
    }

    private static void handleLogout() {
        if (sessionManager.isLoggedIn()) {
            sessionManager.logout();
            history.clear();
            currentSceneData = null;
            cambiaScena("/it/unina/bugboard/fxml/login.fxml", 400, 500, "BugBoard - Login");
        }
    }

    public static void apriPopupModifica(it.unina.bugboard.dto.IssueDTO issue, Runnable onSaveSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    BugBoard.class.getResource("/it/unina/bugboard/fxml/modifica_issue_popup.fxml"));

            javafx.scene.Parent root = loader.load();

            if (loader.getController() instanceof it.unina.bugboard.popup.ModificaIssuePopupController controller) {
                controller.setApiService(issueApiService);
                controller.setData(issue, onSaveSuccess);
            }

            Stage popupStage = new Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(primaryStage);
            popupStage.setTitle("Modifica Issue: " + issue.getTitolo());
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            popupStage.showAndWait();

        } catch (IOException e) {
            mostraAlert(Alert.AlertType.ERROR, "Errore", "Impossibile aprire popup", e.getMessage());
        }
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