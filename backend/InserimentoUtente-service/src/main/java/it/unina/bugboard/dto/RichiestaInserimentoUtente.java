package it.unina.bugboard.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RichiestaInserimentoUtente {

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(min = 2, max = 50, message = "Il nome deve essere tra 2 e 50 caratteri")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    @Size(min = 2, max = 50, message = "Il cognome deve essere tra 2 e 50 caratteri")
    private String cognome;

    @NotBlank(message = "Il codice fiscale è obbligatorio")
    private String codiceFiscale;

    @NotBlank(message = "Il sesso è obbligatorio")
    @Pattern(regexp = "^[MF]$", message = "Il sesso deve essere M o F")
    private String sesso;

    @NotNull(message = "La data di nascita è obbligatoria")
    @Past(message = "La data di nascita deve essere nel passato")
    private LocalDate dataNascita;

    @NotBlank(message = "Lo username è obbligatorio")
    @Size(min = 3, max = 20, message = "Lo username deve essere tra 3 e 20 caratteri")
    private String username;

    @NotBlank(message = "La password è obbligatoria")
    @Size(min = 8, max = 100, message = "La password deve essere di almeno 8 caratteri")
    private String password;

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Email non valida")
    private String mail;

    @JsonProperty("isAdmin")
    private boolean isAdmin;

}
