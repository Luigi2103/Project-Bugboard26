package it.unina.bugboard.dto;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@AllArgsConstructor
public class RispostaLogin {
    private boolean success;
    private String message;
    private String modalita;
    private String token;
    private Long idUtente;
}
