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
import java.util.List;
import java.util.ArrayList;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class AllIssuesController implements Initializable {

    private static final String SORT_BY_DATA_CREAZIONE = "dataCreazione";
    private static final String SORT_DIRECTION_DESC = "DESC";
    private static final String SORT_DIRECTION_ASC = "ASC";

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
                new SortOption("Più recenti", SORT_BY_DATA_CREAZIONE, SORT_DIRECTION_DESC),
                new SortOption("Meno recenti", SORT_BY_DATA_CREAZIONE, SORT_DIRECTION_ASC),
                new SortOption("Priorità (Alta-Bassa)", "priorita", SORT_DIRECTION_DESC),
                new SortOption("Priorità (Bassa-Alta)", "priorita", SORT_DIRECTION_ASC),
                new SortOption("Tipologia", "tipologia", SORT_DIRECTION_ASC),
                new SortOption("Stato", "stato", SORT_DIRECTION_ASC));

        sortComboBox.getSelectionModel().select(0);
        sortComboBox.setOnAction(e -> caricaIssues(0));
    }

    private static class SortOption {
        private final String label;
        private final String sortBy;
        private final String sortDirection;

        public SortOption(String label, String sortBy, String sortDirection) {
            this.label = label;
            this.sortBy = sortBy;
            this.sortDirection = sortDirection;
        }

        public String getSortBy() {
            return sortBy;
        }

        public String getSortDirection() {
            return sortDirection;
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
    private void refreshIssues() {
        caricaIssues(0);
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
            Label noIssues = new Label("Nessuna issue trovata.");
            noIssues.getStyleClass().add("placeholder-text");
            boxIssues.getChildren().add(noIssues);
            return;
        }

        for (IssueDTO issue : currentIssues) {
            HBox issueRow = creaIssueRow(issue);
            boxIssues.getChildren().add(issueRow);
        }
    }

    private void caricaIssues(int page) {
        mostraLoadingIndicator();

        SortOption selectedSort = sortComboBox.getSelectionModel().getSelectedItem();
        String sortBy = selectedSort != null ? selectedSort.getSortBy() : SORT_BY_DATA_CREAZIONE;
        String sortDirection = selectedSort != null ? selectedSort.getSortDirection() : SORT_DIRECTION_DESC;

        Long userId = sessionManager.getUserId();
        if (userId == null)
            return;

        new Thread(() -> {
            RispostaRecuperoIssue response = homeApiService.recuperaIssues(1, null, page, sortBy, sortDirection);

            javafx.application.Platform.runLater(() -> processaRisposta(response, page));
        }).start();
    }

    private void mostraLoadingIndicator() {
        boxIssues.getChildren().clear();
        Label loadingLabel = new Label("Caricamento in corso...");
        loadingLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic; -fx-padding: 20;");
        boxIssues.getChildren().add(loadingLabel);
    }

    private void processaRisposta(RispostaRecuperoIssue response, int page) {
        if (response != null && response.isSuccess() && response.getIssues() != null) {
            mostraIssues(response, page);
        } else {
            mostraErrore();
        }
    }

    private void mostraIssues(RispostaRecuperoIssue response, int page) {
        this.currentIssues = response.getIssues();
        aggiornaPaginazione(page, response.getTotalPages());
        renderIssues();
    }

    private void aggiornaPaginazione(int page, int totalPages) {
        this.currentPage = page;
        this.totalPages = totalPages == 0 ? 1 : totalPages;

        labelPagina.setText("Pagina " + (currentPage + 1) + " di " + this.totalPages);

        btnPrev.setDisable(currentPage == 0);
        btnNext.setDisable(currentPage >= this.totalPages - 1);
    }

    private void mostraErrore() {
        boxIssues.getChildren().clear();
        Label errorLabel = new Label("Impossibile caricare le issue.");
        errorLabel.getStyleClass().add("danger-text");
        boxIssues.getChildren().add(errorLabel);
    }

    @FXML
    private void paginaPrecedente() {
        if (currentPage > 0)
            caricaIssues(currentPage - 1);
    }

    @FXML
    private void paginaSuccessiva() {
        if (currentPage < totalPages - 1)
            caricaIssues(currentPage + 1);
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

        VBox content = creaContenutoIssue(issue);
        VBox buttonsContainer = creaBottoniIssue(issue);

        row.getChildren().addAll(content, buttonsContainer);
        return row;
    }

    private VBox creaContenutoIssue(IssueDTO issue) {
        VBox content = new VBox();
        content.setSpacing(5);
        HBox.setHgrow(content, Priority.ALWAYS);

        Label title = new Label(issue.getTitolo());
        title.getStyleClass().add("issue-card__title");

        Label assigneeLabel = creaAssegnataLabel(issue);

        Label desc = new Label(issue.getDescrizione());
        desc.getStyleClass().add("issue-card__desc");
        desc.setWrapText(true);

        content.getChildren().addAll(title, assigneeLabel, desc);

        if (issue.getTags() != null && !issue.getTags().isEmpty()) {
            FlowPane tagsContainer = creaTagsContainer(issue);
            content.getChildren().add(tagsContainer);
        }

        return content;
    }

    private Label creaAssegnataLabel(IssueDTO issue) {
        String assigneeName = determineAssigneeName(issue);
        Label assigneeLabel = new Label("Assegnata a: " + assigneeName);
        assigneeLabel.setStyle("-fx-text-fill: #3498DB; -fx-font-size: 13px; -fx-font-weight: bold;");
        return assigneeLabel;
    }

    private String determineAssigneeName(IssueDTO issue) {
        if (issue.getNomeAssegnatario() != null) {
            return issue.getNomeAssegnatario() + " "
                    + (issue.getCognomeAssegnatario() != null ? issue.getCognomeAssegnatario() : "");
        } else if (issue.getIdAssegnatario() != null) {
            return "ID: " + issue.getIdAssegnatario();
        }
        return "Non assegnata";
    }

    private FlowPane creaTagsContainer(IssueDTO issue) {
        FlowPane tagsContainer = new FlowPane();
        tagsContainer.setHgap(10);
        for (String tag : issue.getTags()) {
            Label tagLabel = new Label(tag);
            tagLabel.setStyle(
                    "-fx-background-color: #EBF5FB; -fx-text-fill: #2980B9; -fx-padding: 3 8; -fx-background-radius: 12; -fx-border-color: #A9CCE3; -fx-border-radius: 12;");
            tagsContainer.getChildren().add(tagLabel);
        }
        return tagsContainer;
    }

    private VBox creaBottoniIssue(IssueDTO issue) {
        VBox buttonsContainer = new VBox(10);
        buttonsContainer.setAlignment(Pos.CENTER_RIGHT);

        Button btnAction = creaBottoneDettagli(issue);
        buttonsContainer.getChildren().add(btnAction);

        if (sessionManager.isAdmin()) {
            Button btnEdit = creaBottoneModifica(issue);
            buttonsContainer.getChildren().add(btnEdit);
        }

        HBox badgesBox = creaBadges(issue);
        buttonsContainer.getChildren().add(badgesBox);

        return buttonsContainer;
    }

    private Button creaBottoneDettagli(IssueDTO issue) {
        Button btnAction = new Button("Dettagli");
        btnAction.getStyleClass().add("issue-card__btn");
        btnAction.setOnAction(e -> SceneRouter.cambiaScenaConIssue(
                "/it/unina/bugboard/fxml/issue_details.fxml",
                1200, 800,
                "BugBoard - Dettaglio Issue #" + issue.getIdIssue(),
                issue.getIdIssue()));
        return btnAction;
    }

    private Button creaBottoneModifica(IssueDTO issue) {
        Button btnEdit = new Button("Modifica Issue");
        btnEdit.getStyleClass().add("issue-card__btn");
        btnEdit.setOnAction(e -> SceneRouter.apriPopupModifica(issue, () -> caricaIssues(currentPage)));
        return btnEdit;
    }

    private HBox creaBadges(IssueDTO issue) {
        HBox badgesBox = new HBox(10);
        badgesBox.setAlignment(Pos.CENTER_RIGHT);

        Label typeBadge = creaTipologiaBadge(issue);
        Label statusBadge = creaStatoBadge(issue);
        Label priorityBadge = creaPrioritaBadge(issue);

        badgesBox.getChildren().addAll(typeBadge, statusBadge, priorityBadge);
        return badgesBox;
    }

    private Label creaTipologiaBadge(IssueDTO issue) {
        Label typeBadge = new Label(issue.getTipologia());
        typeBadge.setStyle(
                "-fx-background-color: #D1ECF1; -fx-text-fill: #0C5460; -fx-padding: 5 12; -fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 11px;");
        return typeBadge;
    }

    private Label creaStatoBadge(IssueDTO issue) {
        Label statusBadge = new Label(issue.getStato());
        String[] colors = determineStatusColors(issue.getStato());
        statusBadge.setStyle("-fx-background-color: " + colors[0] + "; -fx-text-fill: " + colors[1]
                + "; -fx-padding: 5 12; -fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 11px;");
        return statusBadge;
    }

    private String[] determineStatusColors(String stato) {
        if ("CHIUSA".equalsIgnoreCase(stato)) {
            return new String[] { "#D4EDDA", "#155724" };
        } else if ("IN_CORSO".equalsIgnoreCase(stato) || "IN PROGRESS".equalsIgnoreCase(stato)) {
            return new String[] { "#CCE5FF", "#004085" };
        }
        return new String[] { "#FFF3CD", "#856404" };
    }

    private Label creaPrioritaBadge(IssueDTO issue) {
        String priorita = issue.getPriorita() != null ? issue.getPriorita() : "N/A";
        Label priorityBadge = new Label(priorita);
        String[] colors = determinePriorityColors(issue.getPriorita());
        priorityBadge.setStyle("-fx-background-color: " + colors[0] + "; -fx-text-fill: " + colors[1]
                + "; -fx-padding: 5 12; -fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 11px;");
        return priorityBadge;
    }

    private String[] determinePriorityColors(String priorita) {
        String p = priorita != null ? priorita.toUpperCase() : "";
        if (p.equals("ALTA") || p.equals("MASSIMA")) {
            return new String[] { "#F8D7DA", "#721C24" };
        } else if (p.equals("MEDIA")) {
            return new String[] { "#FFF3CD", "#856404" };
        } else if (p.equals("BASSA")) {
            return new String[] { "#D4EDDA", "#155724" };
        }
        return new String[] { "#E2E3E5", "#383D41" };
    }
}