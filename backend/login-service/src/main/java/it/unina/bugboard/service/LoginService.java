package it.unina.bugboard.service;

import it.unina.bugboard.dto.RichiestaLogin;
import it.unina.bugboard.dto.RichiestaUpdate;
import it.unina.bugboard.dto.RispostaLogin;
import it.unina.bugboard.dto.RispostaUpdate;

public interface LoginService {
    RispostaLogin login(RichiestaLogin loginReq);

    RispostaUpdate update(RichiestaUpdate updateReq);
}
