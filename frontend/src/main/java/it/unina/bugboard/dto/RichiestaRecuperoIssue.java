package it.unina.bugboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RichiestaRecuperoIssue {
    private Integer idProgetto;
    private Integer idAssegnatario;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;

    public RichiestaRecuperoIssue(Integer idProgetto, Integer idAssegnatario) {
        this.idProgetto = idProgetto;
        this.idAssegnatario = idAssegnatario;
    }
}
