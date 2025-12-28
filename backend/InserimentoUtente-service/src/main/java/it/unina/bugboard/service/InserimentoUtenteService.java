package it.unina.bugboard.service;

import it.unina.bugboard.dto.RichiestaInserimentoUtente;
import it.unina.bugboard.dto.RispostaInserimentoUtente;

public interface InserimentoUtenteService {
    RispostaInserimentoUtente inserisciUtente(RichiestaInserimentoUtente richiestaInserimento);
}
