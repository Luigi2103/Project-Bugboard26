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
import lombok.Setter;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ModificaIssuePopupController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(ModificaIssuePopupController.class.getName());

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

    @Setter
    private IssueApiService apiService;
    private IssueDTO currentIssue;
    private Runnable onSuccessCallback;

    private String initialStato;
    private String initialPriorita;

    public void setData(IssueDTO issue, Runnable onSuccess) {
        this.currentIssue = issue;
        this.onSuccessCallback = onSuccess;

        if (issue != null) {
            lblTitolo.setText("Modifica Issue: " + issue.getTitolo());

            this.initialStato = issue.getStato();
            this.initialPriorita = issue.getPriorita();

            if (cmbStato != null) {
                cmbStato.setValue(initialStato);
                cmbStato.valueProperty().addListener((obs, oldVal, newVal) -> checkChanges());
            }
            if (cmbPriorita != null) {
                cmbPriorita.setValue(initialPriorita);
                cmbPriorita.valueProperty().addListener((obs, oldVal, newVal) -> checkChanges());
            }

            btnSalva.setDisable(true);
        }
    }

    private void checkChanges() {
        if (currentIssue == null)
            return;

        String currentStato = cmbStato.getValue();
        String currentPriorita = cmbPriorita.getValue();

        boolean statoChanged = isDifferent(currentStato, initialStato);
        boolean prioritaChanged = isDifferent(currentPriorita, initialPriorita);

        btnSalva.setDisable(!(statoChanged || prioritaChanged));
    }

    private boolean isDifferent(String s1, String s2) {
        return !Objects.equals(s1, s2);
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
            String errorMessage = res != null ? res.getMessage() : "Unknown";
            LOGGER.log(java.util.logging.Level.SEVERE, "Errore modifica: {0}", errorMessage);
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