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
    private Label erroreCodiceFiscale;
    @FXML
    private Label errorePassword;

    @FXML
    public void initialize() {
        pulsanteRegistra.disableProperty().bind(createCampiVuotiBinding());
        // Annulla si abilita quando almeno un campo è compilato (tutti vuoti =
        // disabilitato)
        pulsanteAnnulla.disableProperty().bind(createTuttiCampiVuotiBinding());
        inizializzaListenerRuolo();
        campoDataNascita.setEditable(false);
    }

    private void inizializzaListenerRuolo() {
        gruppoRuolo.selectedToggleProperty().addListener((obs, vecchio, nuovo) -> {
            if (nuovo == null) {
                vecchio.setSelected(true);
            }
        });
    }

    private BooleanBinding createCampiVuotiBinding() {
        return isTrimmedEmpty(campoNome)
                .or(isTrimmedEmpty(campoCognome))
                .or(isTrimmedEmpty(campoCodiceFiscale))
                .or(campoDataNascita.valueProperty().isNull())
                .or(isTrimmedEmpty(campoUsername))
                .or(campoPassword.textProperty().isEmpty())
                .or(campoConfermaPassword.textProperty().isEmpty());
    }

    private BooleanBinding createTuttiCampiVuotiBinding() {
        return isTrimmedEmpty(campoNome)
                .and(isTrimmedEmpty(campoCognome))
                .and(isTrimmedEmpty(campoCodiceFiscale))
                .and(campoDataNascita.valueProperty().isNull())
                .and(isTrimmedEmpty(campoUsername))
                .and(campoPassword.textProperty().isEmpty())
                .and(campoConfermaPassword.textProperty().isEmpty());
    }

    private BooleanBinding isTrimmedEmpty(TextField textField) {
        return Bindings.createBooleanBinding(
                () -> textField.getText() == null || textField.getText().trim().isEmpty(),
                textField.textProperty());
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

        if (!validateInput(password, confermaPassword, codiceFiscale)) {
            return;
        }

        String ruolo = toggleAdmin.isSelected() ? "AMMINISTRATORE" : "UTENTE";

        // TODO eliminare questo metodo, è solo per testare se la GUI funziona
        logUserRegistration(nome, cognome, codiceFiscale, dataNascita, username, ruolo);

        // Reset dei campi dopo registrazione avvenuta con successo (simulato)
        pulisciCampi();
    }

    private boolean validateInput(String password, String confermaPassword, String codiceFiscale) {
        resetErrorStyles();
        boolean isValid = true;

        if (!password.equals(confermaPassword)) {
            setErrorStyle(campoPassword);
            setErrorStyle(campoConfermaPassword);
            mostraErroreInline(errorePassword, "Le password non coincidono");
            isValid = false;
        }

        if (codiceFiscale.length() != 16) {
            setErrorStyle(campoCodiceFiscale);
            mostraErroreInline(erroreCodiceFiscale, "Codice Fiscale non valido (deve essere 16 caratteri)");
            isValid = false;
        }

        return isValid;
    }

    private void setErrorStyle(javafx.scene.control.Control control) {
        if (!control.getStyleClass().contains("text-field-error")) {
            control.getStyleClass().add("text-field-error");
        }
    }

    private void removeErrorStyle(javafx.scene.control.Control control) {
        control.getStyleClass().remove("text-field-error");
    }

    private void resetErrorStyles() {
        removeErrorStyle(campoPassword);
        removeErrorStyle(campoConfermaPassword);
        removeErrorStyle(campoCodiceFiscale);
        if (etichettaErrore != null) {
            etichettaErrore.setVisible(false);
        }
        if (erroreCodiceFiscale != null) {
            erroreCodiceFiscale.setVisible(false);
            erroreCodiceFiscale.setManaged(false);
        }
        if (errorePassword != null) {
            errorePassword.setVisible(false);
            errorePassword.setManaged(false);
        }
    }

    private void mostraErroreInline(Label label, String messaggio) {
        if (label != null) {
            label.setText(messaggio);
            label.setVisible(true);
            label.setManaged(true);
        }
    }

    private void logUserRegistration(String nome, String cognome, String codiceFiscale, LocalDate dataNascita,
            String username, String ruolo) {
        // Qui andrebbe la logica per salvare l'utente
        System.out.println("Registrazione utente:");
        System.out.println("Nome: " + nome);
        System.out.println("Cognome: " + cognome);
        System.out.println("Codice Fiscale: " + codiceFiscale);
        System.out.println("Data di Nascita: "
                + (dataNascita != null ? dataNascita.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"));
        System.out.println("Username: " + username);
        System.out.println("Ruolo: " + ruolo);
    }

    @FXML
    private void annulla() {
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

        resetErrorStyles();

        // Reset del toggle al valore predefinito (Utente)
        toggleUtente.setSelected(true);
    }
}
