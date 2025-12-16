package it.unina.bugboard.homepage;

import it.unina.bugboard.common.SessionManager;
import it.unina.bugboard.dto.IssueDTO;
import it.unina.bugboard.dto.RispostaRecuperoIssue;
import it.unina.bugboard.navigation.SceneRouter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
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

        boolean admin = sessionManager.isAdmin();
        if (btnAggiungiUtente != null) {
            btnAggiungiUtente.setVisible(admin);
            btnAggiungiUtente.setManaged(admin);
        }

        if (btnIssueProgetto != null) {
            btnIssueProgetto.setVisible(admin);
            btnIssueProgetto.setManaged(admin);
        }

        caricaIssues();
    }

    private void caricaIssues() {
        boxIssues.getChildren().clear();

        Long userId = sessionManager.getUserId();
        if (userId == null) {
            return;
        }

        RispostaRecuperoIssue response = homeApiService.recuperaIssues(1, userId.intValue());

        if (response != null && response.isSuccess() && response.getIssues() != null) {
            int count = 0;
            for (IssueDTO issue : response.getIssues()) {
                if (count >= 4)
                    break;

                HBox issueRow = creaIssueRow(issue);
                boxIssues.getChildren().add(issueRow);
                count++;
            }

            if (count == 0) {
                Label noIssues = new Label("Nessuna issue assegnata.");
                boxIssues.getChildren().add(noIssues);
            }
        } else {
            Label noIssues = new Label("Nessuna issue assegnata o errore nel caricamento.");
            boxIssues.getChildren().add(noIssues);
        }
    }

    private HBox creaIssueRow(IssueDTO issue) {
        HBox row = new HBox();
        row.getStyleClass().add("issue-card");
        row.setSpacing(20);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        ImageView img = new ImageView();
        img.setFitHeight(50);
        img.setFitWidth(50);
        img.setPreserveRatio(true);
        img.getStyleClass().add("issue-card__image");

        VBox content = new VBox();
        content.setSpacing(5);
        HBox.setHgrow(content, Priority.ALWAYS);

        Label title = new Label(issue.getTitolo());
        title.getStyleClass().add("issue-card__title");

        Label desc = new Label(issue.getDescrizione());
        desc.getStyleClass().add("issue-card__desc");
        desc.setWrapText(true);

        content.getChildren().addAll(title, desc);

        if (issue.getTags() != null && !issue.getTags().isEmpty()) {
            FlowPane tagsContainer = new FlowPane();
            tagsContainer.getStyleClass().add("tags-container");

            for (String tag : issue.getTags()) {
                Label tagLabel = new Label(tag);
                tagLabel.getStyleClass().add("tag-label");
                // FIX: Forziamo lo stile inline per assicurarci che si vedano
                tagLabel.setStyle(
                        "-fx-background-color: #EBF5FB; -fx-text-fill: #2980B9; -fx-padding: 3 8; -fx-background-radius: 12; -fx-border-color: #A9CCE3; -fx-border-radius: 12;");
                tagsContainer.getChildren().add(tagLabel);
            }
            content.getChildren().add(tagsContainer);
        }

        Button btnAction = new Button("Dettagli");
        btnAction.getStyleClass().add("issue-card__btn");
        btnAction.setOnAction(e -> {
            SceneRouter.cambiaScenaConIssue(
                    "/it/unina/bugboard/fxml/issue_details.fxml",
                    1200, 800,
                    "BugBoard - Dettaglio Issue #" + issue.getIdIssue(),
                    issue.getIdIssue());
        });

        Button btnEdit = new Button("Modifica Issue");
        btnEdit.getStyleClass().add("issue-card__btn");
        btnEdit.setOnAction(e -> {
            System.out.println("Modifica issue " + issue.getIdIssue());
            // TODO: Implement edit logic
        });

        VBox buttonsContainer = new VBox(10); // Spacing 10
        buttonsContainer.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        buttonsContainer.getChildren().addAll(btnAction, btnEdit);

        row.getChildren().addAll(img, content, buttonsContainer);
        return row;
    }

    @FXML
    private Button btnIssueProgetto;

    @FXML
    private void vaiAInserimentoUtente() {
        SceneRouter.cambiaScena("/it/unina/bugboard/fxml/insert_user.fxml",
                1000, 900, "BugBoard - Registra Nuovo Utente");
    }

    @FXML
    private void segnalaIssue() {
        SceneRouter.cambiaScena("/it/unina/bugboard/fxml/insert_issue.fxml",
                1000, 800, "BugBoard - Nuova Issue");
    }

    @FXML
    private void vediMieIssue() {
        // Logica per vedere le issue assegnate all'utente
    }

    @FXML
    private void vediIssueProgetto() {
        // Logica per vedere tutte le issue del progetto (admin)
    }

    @FXML
    private void vediTutte() {
        // Metodo legacy/backup, reindirizza alle mie issue per deafult
        vediMieIssue();
    }

    @FXML
    private void logout() {
        sessionManager.logout();
        SceneRouter.cambiaScena("/it/unina/bugboard/fxml/login.fxml",
                1000, 850, "BugBoard - Login");
    }
}
