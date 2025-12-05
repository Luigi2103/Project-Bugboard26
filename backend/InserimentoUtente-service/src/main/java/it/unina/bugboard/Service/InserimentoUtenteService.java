package it.unina.bugboard.Service;

import it.unina.bugboard.dto.RichiestaInserimentoUtente;
import it.unina.bugboard.dto.RispostaInserimentoUtente;

public interface InserimentoUtenteService {
    RispostaInserimentoUtente InserisciUtente(RichiestaInserimentoUtente richiestaInserimento);
}
