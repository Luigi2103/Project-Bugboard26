package it.unina.bugboard.popup;

import it.unina.bugboard.dto.IssueDTO;
import it.unina.bugboard.issuedetails.RispostaDettaglioIssue;
import it.unina.bugboard.issuedetails.IssueApiService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ModificaIssuePopupController implements Initializable {

    @FXML
    private Label lblTitolo;
    @FXML
    private ComboBox<String> cmbStato;
    @FXML
    private ComboBox<String> cmbPriorita;
    @FXML
    private Button btnSalva;
    @FXML
    private Button btnAnnulla;

    private IssueApiService apiService;
    private IssueDTO currentIssue;
    private Runnable onSuccessCallback;

    private String initialStato;
    private String initialPriorita;

    public void setApiService(IssueApiService apiService) {
        this.apiService = apiService;
    }

    public void setData(IssueDTO issue, Runnable onSuccess) {
        this.currentIssue = issue;
        this.onSuccessCallback = onSuccess;

        if (issue != null) {
            lblTitolo.setText("Modifica Issue: " + issue.getTitolo());

            this.initialStato = issue.getStato();
            this.initialPriorita = issue.getPriorita();

            if (cmbStato != null) {
                cmbStato.setValue(initialStato);
                // Listener per cambiamenti
                cmbStato.valueProperty().addListener((obs, oldVal, newVal) -> checkChanges());
            }
            if (cmbPriorita != null) {
                cmbPriorita.setValue(initialPriorita);
                // Listener per cambiamenti
                cmbPriorita.valueProperty().addListener((obs, oldVal, newVal) -> checkChanges());
            }

            // Disabilita inizialmente il pulsante salva
            btnSalva.setDisable(true);
        }
    }

    private void checkChanges() {
        if (currentIssue == null)
            return;

        String currentStato = cmbStato.getValue();
        String currentPriorita = cmbPriorita.getValue();

        boolean statoChanged = !isSame(currentStato, initialStato);
        boolean prioritaChanged = !isSame(currentPriorita, initialPriorita);

        // Abilita salva solo se qualcosa è cambiato
        btnSalva.setDisable(!(statoChanged || prioritaChanged));
    }

    private boolean isSame(String s1, String s2) {
        if (s1 == null && s2 == null)
            return true;
        if (s1 == null || s2 == null)
            return false;
        return s1.equals(s2);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cmbStato != null) {
            cmbStato.getItems().addAll("TO-DO", "IN PROGRESS", "RESOLVED", "CLOSED");
        }
        if (cmbPriorita != null) {
            cmbPriorita.getItems().addAll("BASSA", "MEDIA", "ALTA", "MASSIMA");
        }
    }

    @FXML
    private void salva() {
        if (currentIssue == null || apiService == null)
            return;

        String stato = cmbStato.getValue();
        String priorita = cmbPriorita.getValue();

        RispostaDettaglioIssue res = apiService.modificaIssue(currentIssue.getIdIssue(), stato, priorita);

        if (res != null && res.isSuccess()) {
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
            chiudi();
        } else {
            // TODO: Mostrare errore? Per ora stampiamo
            System.err.println("Errore modifica: " + (res != null ? res.getMessage() : "Unknown"));
        }
    }

    @FXML
    private void annulla() {
        chiudi();
    }

    private void chiudi() {
        Stage stage = (Stage) btnAnnulla.getScene().getWindow();
        stage.close();
    }
}
