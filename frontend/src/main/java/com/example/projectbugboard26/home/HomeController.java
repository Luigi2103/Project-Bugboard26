package com.example.projectbugboard26.home;

import com.example.projectbugboard26.navigation.SceneRouter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private ImageView iconaUtente;
    @FXML
    private Label labelBenvenuto;
    @FXML
    private TextField campoRicercaTask;
    @FXML
    private ListView<String> listaTasks;
    @FXML
    private HBox carouselIssues;
    @FXML
    private MenuButton menuNavigazione;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Recuperare l'utente loggato dalla sessione o dal contesto globale
        // TODO: Impostare l'immagine dell'utente (iconaUtente)
        // TODO: Impostare il messaggio di benvenuto con il nome dell'utente (labelBenvenuto)

        // TODO: Recuperare la lista dei progetti dal database
        // TODO: Popolare 'listaTasks' con i nomi dei progetti recuperati dal DB

        // TODO: Configurare il listener per la ricerca su 'campoRicercaTask'

        // TODO: Configurare il listener per la selezione del progetto in 'listaTasks'
        // TODO: Al click su un progetto, caricare le issue associate e popolare 'carouselIssues'
    }

    // Navigation Actions

    @FXML
    private void vaiAInserimentoIssue() {
        // TODO: Implementare navigazione verso Inserimento Issue
        System.out.println("TODO: Navigazione verso Inserimento Issue");
    }

    @FXML
    private void vaiAInserimentoUtente() {
        // TODO: Implementare navigazione verso Inserimento Utente
        SceneRouter.cambiaScena("/com/example/projectbugboard26/fxml/insert_user.fxml", 900, 800, "BugBoard - Registra Utente");
    }

    @FXML
    private void logout() {
        // TODO: Implementare logica di logout
        SceneRouter.cambiaScena("/com/example/projectbugboard26/fxml/login.fxml", 800, 600, "BugBoard - Login");
    }
}
