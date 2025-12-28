package it.unina.bugboard.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class RispostaUpdate {
    private String message;
    private boolean success;
}
