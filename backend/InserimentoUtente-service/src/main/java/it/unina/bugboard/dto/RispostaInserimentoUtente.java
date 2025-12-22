package it.unina.bugboard.dto;

import java.util.Map;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class RispostaInserimentoUtente {

    private boolean success;
    private String message;
    private Map<String, String> fieldErrors;



    public RispostaInserimentoUtente(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}