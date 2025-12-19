package it.unina.bugboard.dto;

import lombok.*;

@Getter
@Setter
public class RichiestaRecuperoIssue {
    private Integer idProgetto;
    private Integer idAssegnatario;
    private Integer page; 
    private Integer size; 
    private String sortBy;
    private String sortDirection;
}
