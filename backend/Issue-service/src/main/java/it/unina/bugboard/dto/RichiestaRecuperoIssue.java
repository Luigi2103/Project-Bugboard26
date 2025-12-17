package it.unina.bugboard.dto;

import lombok.*;

@Getter
@Setter
public class RichiestaRecuperoIssue {
    private Integer idProgetto;
    private Integer idAssegnatario;
    private Integer page; // Default 0
    private Integer size; // Default 10
}
