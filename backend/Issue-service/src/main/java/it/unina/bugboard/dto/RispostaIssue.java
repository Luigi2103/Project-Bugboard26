package it.unina.bugboard.dto;
import lombok.*;

@Getter
@AllArgsConstructor

public class RispostaIssue {

    private String message;
    private boolean success;

}
