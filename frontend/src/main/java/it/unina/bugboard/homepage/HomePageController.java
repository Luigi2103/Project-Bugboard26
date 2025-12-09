package it.unina.bugboard.homepage;

import it.unina.bugboard.common.SessionManager;
import it.unina.bugboard.navigation.SceneRouter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.net.URL;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {

    @FXML
    private Label etichettaBenvenuto;
    @FXML
    private Button btnAggiungiUtente;
    @FXML
    private VBox boxIssues;

    private final HomeApiService homeApiService;
    private final SessionManager sessionManager;

    public HomePageController(HomeApiService homeApiService, SessionManager sessionManager) {
        this.homeApiService = homeApiService;
        this.sessionManager = sessionManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String username = sessionManager.getUsername() != null
                ? sessionManager.getUsername()
                : "Utente";

        etichettaBenvenuto.setText("Ciao, " + username);

        // Permessi admin
        boolean admin = sessionManager.isAdmin();
        if (btnAggiungiUtente != null) {
            btnAggiungiUtente.setVisible(admin);
            btnAggiungiUtente.setManaged(admin);
        }

        caricaIssuesMock();
    }

    private void caricaIssuesMock() {
        boxIssues.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            HBox issueRow = creaIssueRowMock(i);
            boxIssues.getChildren().add(issueRow);
        }
    }

    private HBox creaIssueRowMock(int index) {
        HBox row = new HBox();
        row.getStyleClass().add("issue-card");
        row.setSpacing(20);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        ImageView img = new ImageView();
        img.setFitHeight(50);
        img.setFitWidth(50);
        img.setPreserveRatio(true);
        img.getStyleClass().add("issue-card__image");
        // Placeholder image logic or color square could be added here if needed

        VBox content = new VBox();
        content.setSpacing(5);
        HBox.setHgrow(content, Priority.ALWAYS);

        Label title = new Label("Issue #" + (index + 1) + " - Bug nel login");
        title.getStyleClass().add("issue-card__title");

        Label desc = new Label("Si verifica un errore quando l'utente prova a fare login con caratteri speciali...");
        desc.getStyleClass().add("issue-card__desc");
        desc.setWrapText(true);

        content.getChildren().addAll(title, desc);

        Button btnAction = new Button("Dettagli");
        btnAction.getStyleClass().add("issue-card__btn");

        row.getChildren().addAll(img, content, btnAction);
        return row;
    }

    @FXML
    private void vaiAInserimentoUtente() {
        SceneRouter.cambiaScena("/it/unina/bugboard/fxml/insert_user.fxml",
                1000, 900, "BugBoard - Aggiungi Utente");
    }

    @FXML
    private void segnalaIssue() {
        SceneRouter.cambiaScena("/it/unina/bugboard/fxml/insert_issue.fxml",
                1000, 800, "BugBoard - Nuova Issue");
    }

    @FXML
    private void vediTutte() {
        System.out.println("Vedi tutte clicked");
    }

    @FXML
    private void logout() {
        sessionManager.logout();
        SceneRouter.cambiaScena("/it/unina/bugboard/fxml/login.fxml",
                1000, 850, "BugBoard - Login");
    }
}
