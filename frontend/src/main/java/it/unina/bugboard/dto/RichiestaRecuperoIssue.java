package it.unina.bugboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RichiestaRecuperoIssue {
    private Integer idProgetto;
    private Integer idAssegnatario;
    private Integer page;
    private Integer size;

    public RichiestaRecuperoIssue(Integer idProgetto, Integer idAssegnatario) {
        this.idProgetto = idProgetto;
        this.idAssegnatario = idAssegnatario;
    }

    public Integer getIdProgetto() {
        return idProgetto;
    }

    public void setIdProgetto(Integer idProgetto) {
        this.idProgetto = idProgetto;
    }

    public Integer getIdAssegnatario() {
        return idAssegnatario;
    }

    public void setIdAssegnatario(Integer idAssegnatario) {
        this.idAssegnatario = idAssegnatario;
    }
}
