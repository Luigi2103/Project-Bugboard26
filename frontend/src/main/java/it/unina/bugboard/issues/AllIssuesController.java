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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
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
    @FXML
    private ComboBox<SortOption> sortComboBox;

    // Store current issues for client-side sorting
    private List<IssueDTO> currentIssues = new ArrayList<>();

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

        inizializzaOrdinamento();
        caricaIssues(0);
    }

    private void inizializzaOrdinamento() {
        sortComboBox.getItems().addAll(
                new SortOption("Più recenti",
                        Comparator.comparing(IssueDTO::getDataCreazione, Comparator.nullsLast(String::compareTo))
                                .reversed()),
                new SortOption("Meno recenti",
                        Comparator.comparing(IssueDTO::getDataCreazione, Comparator.nullsLast(String::compareTo))),
                new SortOption("Priorità (Alta-Bassa)",
                        (i1, i2) -> comparePriorita(i1.getPriorita(), i2.getPriorita())),
                new SortOption("Priorità (Bassa-Alta)",
                        (i1, i2) -> comparePriorita(i2.getPriorita(), i1.getPriorita())), // Reversed logic for
                                                                                          // Bassa-Alta
                new SortOption("Tipologia",
                        Comparator.comparing(IssueDTO::getTipologia, Comparator.nullsLast(String::compareTo))),
                new SortOption("Stato",
                        Comparator.comparing(IssueDTO::getStato, Comparator.nullsLast(String::compareTo))));

        sortComboBox.getSelectionModel().select(0);
        sortComboBox.setOnAction(e -> renderIssues());
    }

    private int comparePriorita(String p1, String p2) {
        if (p1 == null)
            p1 = "BASSA";
        if (p2 == null)
            p2 = "BASSA";
        return getPrioritaValore(p2) - getPrioritaValore(p1); // Descending order: Massima > Bassa
    }

    private int getPrioritaValore(String p) {
        switch (p.toUpperCase()) {
            case "MASSIMA":
                return 4;
            case "ALTA":
                return 3;
            case "MEDIA":
                return 2;
            case "BASSA":
                return 1;
            default:
                return 0;
        }
    }

    private static class SortOption {
        private final String label;
        private final Comparator<IssueDTO> comparator;

        public SortOption(String label, Comparator<IssueDTO> comparator) {
            this.label = label;
            this.comparator = comparator;
        }

        public Comparator<IssueDTO> getComparator() {
            return comparator;
        }

        @Override
        public String toString() {
            return label;
        }
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

    private void renderIssues() {
        boxIssues.getChildren().clear();
        if (currentIssues == null || currentIssues.isEmpty()) {
            return;
        }

        SortOption selectedSort = sortComboBox.getSelectionModel().getSelectedItem();
        if (selectedSort != null) {
            currentIssues.sort(selectedSort.getComparator());
        }

        for (IssueDTO issue : currentIssues) {
            HBox issueRow = creaIssueRow(issue);
            boxIssues.getChildren().add(issueRow);
        }
    }

    private void caricaIssues(int page) {
        // Show loading state
        boxIssues.getChildren().clear();
        Label loadingLabel = new Label("Caricamento in corso...");
        loadingLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic; -fx-padding: 20;");
        boxIssues.getChildren().add(loadingLabel);

        Long userId = sessionManager.getUserId();
        if (userId == null)
            return;

        // Run in background thread to avoid blocking UI
        new Thread(() -> {
            RispostaRecuperoIssue response = homeApiService.recuperaIssues(1, null, page);

            // Update UI on JavaFX Application Thread
            javafx.application.Platform.runLater(() -> {
                if (response != null && response.isSuccess() && response.getIssues() != null) {
                    this.currentIssues = response.getIssues();

                    this.currentPage = page;
                    this.totalPages = response.getTotalPages();
                    if (this.totalPages == 0)
                        this.totalPages = 1;

                    labelPagina.setText("Pagina " + (currentPage + 1) + " di " + totalPages);

                    btnPrev.setDisable(currentPage == 0);
                    btnNext.setDisable(currentPage >= totalPages - 1);

                    renderIssues();
                } else {
                    boxIssues.getChildren().clear();
                    Label errorLabel = new Label("Impossibile caricare le issue.");
                    errorLabel.getStyleClass().add("danger-text");
                    boxIssues.getChildren().add(errorLabel);
                }
            });
        }).start();
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
        tornaAllaHome();
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

        // --- BADGES ROW (Under Buttons) ---
        HBox badgesBox = new HBox(10);
        badgesBox.setAlignment(Pos.CENTER_RIGHT);
        // badgesBox.setPadding(new javafx.geometry.Insets(10, 0, 0, 0));

        // 1. Tipologia
        Label typeBadge = new Label(issue.getTipologia());
        typeBadge.setStyle(
                "-fx-background-color: #D1ECF1; -fx-text-fill: #0C5460; -fx-padding: 5 12; -fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 11px;");

        // 2. Stato
        Label statusBadge = new Label(issue.getStato());
        String statusColor = "#FFF3CD"; // Default APERTA (Yellow)
        String statusText = "#856404";
        if ("CHIUSA".equalsIgnoreCase(issue.getStato())) {
            statusColor = "#D4EDDA"; // Green
            statusText = "#155724";
        } else if ("IN_CORSO".equalsIgnoreCase(issue.getStato()) || "IN PROGRESS".equalsIgnoreCase(issue.getStato())) {
            statusColor = "#CCE5FF"; // Blue
            statusText = "#004085";
        }
        statusBadge.setStyle("-fx-background-color: " + statusColor + "; -fx-text-fill: " + statusText
                + "; -fx-padding: 5 12; -fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 11px;");

        // 3. Priorità
        Label priorityBadge = new Label(issue.getPriorita() != null ? issue.getPriorita() : "N/A");
        String priorityColor = "#E2E3E5"; // Default Gray
        String priorityText = "#383D41";
        String p = issue.getPriorita() != null ? issue.getPriorita().toUpperCase() : "";
        if (p.equals("ALTA") || p.equals("MASSIMA")) {
            priorityColor = "#F8D7DA"; // Red
            priorityText = "#721C24";
        } else if (p.equals("MEDIA")) {
            priorityColor = "#FFF3CD"; // Orange/Yellow
            priorityText = "#856404";
        } else if (p.equals("BASSA")) {
            priorityColor = "#D4EDDA"; // Green
            priorityText = "#155724";
        }
        priorityBadge.setStyle("-fx-background-color: " + priorityColor + "; -fx-text-fill: " + priorityText
                + "; -fx-padding: 5 12; -fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 11px;");

        badgesBox.getChildren().addAll(typeBadge, statusBadge, priorityBadge);
        buttonsContainer.getChildren().add(badgesBox);
        // ----------------------------------

        row.getChildren().addAll(content, buttonsContainer);
        return row;
    }
}
