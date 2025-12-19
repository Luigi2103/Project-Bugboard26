package it.unina.bugboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RichiestaModificaIssue {
    private String stato;
    private String priorita;
}
