package com.example.projectbugboard26.inserimentoutente;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.Bindings;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InsertUserController {

    @FXML
    private VBox contenitoreInserimento;

    @FXML
    private TextField campoNome;

    @FXML
    private TextField campoCognome;

    @FXML
    private TextField campoCodiceFiscale;

    @FXML
    private DatePicker campoDataNascita;

    @FXML
    private TextField campoUsername;

    @FXML
    private PasswordField campoPassword;

    @FXML
    private PasswordField campoConfermaPassword;

    @FXML
    private Button pulsanteRegistra;

    @FXML
    private Button pulsanteAnnulla;

    @FXML
    private Label etichettaErrore;

    @FXML
    private ToggleGroup gruppoRuolo;

    @FXML
    private ToggleButton toggleUtente;

    @FXML
    private ToggleButton toggleAdmin;

    @FXML
    public void initialize() {
        // Binding per abilitare il pulsante Registra solo se tutti i campi sono
        // compilati
        BooleanBinding campiVuoti = Bindings.createBooleanBinding(() -> campoNome.getText().trim().isEmpty() ||
                campoCognome.getText().trim().isEmpty() ||
                campoCodiceFiscale.getText().trim().isEmpty() ||
                campoDataNascita.getValue() == null ||
                campoUsername.getText().trim().isEmpty() ||
                campoPassword.getText().isEmpty() ||
                campoConfermaPassword.getText().isEmpty(),
                campoNome.textProperty(),
                campoCognome.textProperty(),
                campoCodiceFiscale.textProperty(),
                campoDataNascita.valueProperty(),
                campoUsername.textProperty(),
                campoPassword.textProperty(),
                campoConfermaPassword.textProperty());

        pulsanteRegistra.disableProperty().bind(campiVuoti);
    }

    @FXML
    private void registraUtente() {
        String nome = campoNome.getText();
        String cognome = campoCognome.getText();
        String codiceFiscale = campoCodiceFiscale.getText();
        LocalDate dataNascita = campoDataNascita.getValue();
        String username = campoUsername.getText();
        String password = campoPassword.getText();
        String confermaPassword = campoConfermaPassword.getText();

        // Validazione password
        if (!password.equals(confermaPassword)) {
            mostraErrore("Le password non coincidono.");
            return;
        }

        // Validazione codice fiscale (lunghezza base)
        if (codiceFiscale.length() != 16) {
            mostraErrore("Il codice fiscale deve essere di 16 caratteri.");
            return;
        }

        // Determina il ruolo selezionato
        String ruolo = toggleAdmin.isSelected() ? "AMMINISTRATORE" : "UTENTE";

        // Qui andrebbe la logica per salvare l'utente
        System.out.println("Registrazione utente:");
        System.out.println("Nome: " + nome);
        System.out.println("Cognome: " + cognome);
        System.out.println("Codice Fiscale: " + codiceFiscale);
        System.out.println("Data di Nascita: " + dataNascita.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        System.out.println("Username: " + username);
        System.out.println("Ruolo: " + ruolo);

        // Reset dei campi dopo registrazione avvenuta con successo (simulato)
        pulisciCampi();
    }

    @FXML
    private void annulla() {
        // Logica per tornare indietro o chiudere la schermata
        System.out.println("Operazione annullata");
        pulisciCampi();
    }

    private void mostraErrore(String messaggio) {
        if (etichettaErrore != null) {
            etichettaErrore.setText(messaggio);
            etichettaErrore.setVisible(true);
        }
    }

    private void pulisciCampi() {
        campoNome.clear();
        campoCognome.clear();
        campoCodiceFiscale.clear();
        campoDataNascita.setValue(null);
        campoUsername.clear();
        campoPassword.clear();
        campoConfermaPassword.clear();
        if (etichettaErrore != null) {
            etichettaErrore.setVisible(false);
        }
        // Reset del toggle al valore predefinito (Utente)
        toggleUtente.setSelected(true);
    }
}
