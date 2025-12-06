package it.unina.bugboard.homepage;

import it.unina.bugboard.common.SessionManager;
import it.unina.bugboard.navigation.SceneRouter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {

    @FXML private Label etichettaBenvenuto;
    @FXML private Button btnAggiungiUtente;
    @FXML private FlowPane boxIssues;

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

        etichettaBenvenuto.setText("Ciao " + username);

        // Permessi admin
        boolean admin = sessionManager.isAdmin();
        btnAggiungiUtente.setVisible(admin);
        btnAggiungiUtente.setManaged(admin);

        caricaIssuesMock();
    }

    private void caricaIssuesMock() {
        for (int i = 0; i < 4; i++) {
            VBox issueCard = creaIssueCardMock(i);
            boxIssues.getChildren().add(issueCard);
        }
    }

    private VBox creaIssueCardMock(int index) {
        VBox card = new VBox();
        card.getStyleClass().add("issue-card");

        ImageView img = new ImageView();
        img.setFitHeight(100);
        img.setFitWidth(150);
        img.setPreserveRatio(true);
        img.getStyleClass().add("issue-card__image");

        Label title = new Label("Issue #" + (index + 1));
        title.getStyleClass().add("issue-card__title");

        Label desc = new Label("Descrizione breve dell’issue " + (index + 1) + "...");
        desc.getStyleClass().add("issue-card__desc");
        desc.setWrapText(true);

        Button btnAction = new Button("Modifica");
        btnAction.getStyleClass().add("issue-card__btn");

        card.getChildren().addAll(img, title, desc, btnAction);
        return card;
    }

    @FXML
    private void vaiAInserimentoUtente() {
        SceneRouter.cambiaScena("/it/unina/bugboard/fxml/insert_user.fxml",
                900, 930, "BugBoard - Aggiungi Utente");
    }

    @FXML
    private void segnalaIssue() { System.out.println("Segnala Issue clicked"); }

    @FXML
    private void vediTutte() { System.out.println("Vedi tutte clicked"); }

    @FXML
    private void logout() {
        sessionManager.logout();
        SceneRouter.cambiaScena("/it/unina/bugboard/fxml/login.fxml",
                900, 800, "BugBoard - Login");
    }
}
