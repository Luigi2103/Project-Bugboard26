package it.unina.bugboard.issues;

import it.unina.bugboard.common.SessionManager;
import it.unina.bugboard.dto.IssueDTO;
import it.unina.bugboard.dto.RispostaRecuperoIssue;

import it.unina.bugboard.homepage.HomeApiService;
import it.unina.bugboard.navigation.SceneRouter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class AllIssuesController implements Initializable {

    @FXML
    private VBox boxIssues;
    @FXML
    private Button btnPrev;
    @FXML
    private Button btnNext;
    @FXML
    private Label labelPagina;
    @FXML
    private Button btnAggiungiUtente;

    private final HomeApiService homeApiService;
    private final SessionManager sessionManager;

    private int currentPage = 0;
    private int totalPages = 0;

    public AllIssuesController(HomeApiService homeApiService, SessionManager sessionManager) {
        this.homeApiService = homeApiService;
        this.sessionManager = sessionManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (btnAggiungiUtente != null) {
            boolean admin = sessionManager.isAdmin();
            btnAggiungiUtente.setVisible(admin);
            btnAggiungiUtente.setManaged(admin);
        }

        caricaIssues(0);
    }

    @FXML
    private void tornaAllaHome() {
        SceneRouter.cambiaScena("/it/unina/bugboard/fxml/home.fxml", 1200, 800, "BugBoard - Home");
    }

    @FXML
    private void segnalaIssue() {
        SceneRouter.cambiaScena("/it/unina/bugboard/fxml/insert_issue.fxml", 1000, 800, "BugBoard - Nuova Issue");
    }

    @FXML
    private void vaiAInserimentoUtente() {
        SceneRouter.cambiaScena("/it/unina/bugboard/fxml/insert_user.fxml", 1000, 900,
                "BugBoard - Registra Nuovo Utente");
    }

    @FXML
    private void logout() {
        sessionManager.logout();
        SceneRouter.cambiaScena("/it/unina/bugboard/fxml/login.fxml", 1000, 850, "BugBoard - Login");
    }

    private void caricaIssues(int page) {
        boxIssues.getChildren().clear();
        Long userId = sessionManager.getUserId();
        if (userId == null)
            return;

        RispostaRecuperoIssue response = homeApiService.recuperaIssues(1, null, page);

        if (response != null && response.isSuccess() && response.getIssues() != null) {
            for (IssueDTO issue : response.getIssues()) {
                HBox issueRow = creaIssueRow(issue);
                boxIssues.getChildren().add(issueRow);
            }

            this.currentPage = page;
            this.totalPages = response.getTotalPages();
            if (this.totalPages == 0)
                this.totalPages = 1;

            labelPagina.setText("Pagina " + (currentPage + 1) + " di " + totalPages);

            btnPrev.setDisable(currentPage == 0);
            btnNext.setDisable(currentPage >= totalPages - 1);
        } else {
            Label errorLabel = new Label("Impossibile caricare le issue.");
            errorLabel.getStyleClass().add("danger-text");
            boxIssues.getChildren().add(errorLabel);
        }
    }

    @FXML
    private void paginaPrecedente() {
        if (currentPage > 0) {
            caricaIssues(currentPage - 1);
        }
    }

    @FXML
    private void paginaSuccessiva() {
        if (currentPage < totalPages - 1) {
            caricaIssues(currentPage + 1);
        }
    }

    @FXML
    private void tornaIndietro() {
        SceneRouter.tornaIndietro();
    }

    private HBox creaIssueRow(IssueDTO issue) {
        HBox row = new HBox();
        row.getStyleClass().add("issue-card");
        row.setSpacing(20);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox();
        content.setSpacing(5);
        HBox.setHgrow(content, Priority.ALWAYS);

        // Title
        Label title = new Label(issue.getTitolo());
        title.getStyleClass().add("issue-card__title");

        // Assignee
        String assigneeName = "Non assegnata";
        if (issue.getNomeAssegnatario() != null) {
            assigneeName = issue.getNomeAssegnatario() + " "
                    + (issue.getCognomeAssegnatario() != null ? issue.getCognomeAssegnatario() : "");
        } else if (issue.getIdAssegnatario() != null) {
            assigneeName = "ID: " + issue.getIdAssegnatario();
        }
        Label assigneeLabel = new Label("Assegnata a: " + assigneeName);
        assigneeLabel.setStyle("-fx-text-fill: #3498DB; -fx-font-size: 13px; -fx-font-weight: bold;");

        // Description
        Label desc = new Label(issue.getDescrizione());
        desc.getStyleClass().add("issue-card__desc");
        desc.setWrapText(true);

        content.getChildren().addAll(title, assigneeLabel, desc);

        if (issue.getTags() != null && !issue.getTags().isEmpty()) {
            FlowPane tagsContainer = new FlowPane();
            tagsContainer.setHgap(10);
            for (String tag : issue.getTags()) {
                Label tagLabel = new Label(tag);
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

        VBox buttonsContainer = new VBox(10);
        buttonsContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonsContainer.getChildren().add(btnAction);

        if (sessionManager.isAdmin()) {
            Button btnEdit = new Button("Modifica Issue");
            btnEdit.getStyleClass().add("issue-card__btn");

            btnEdit.setOnAction(e -> {
                System.out.println("Modifica issue " + issue.getIdIssue());
            });
            buttonsContainer.getChildren().add(btnEdit);
        }

        row.getChildren().addAll(content, buttonsContainer);
        return row;
    }
}
