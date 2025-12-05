package it.unina.bugboard.service;

import it.unina.bugboard.DTO.RichiestaLogin;
import it.unina.bugboard.DTO.RichiestaUpdate;
import it.unina.bugboard.DTO.RispostaLogin;
import it.unina.bugboard.DTO.RispostaUpdate;

public interface LoginService {
    RispostaLogin login(RichiestaLogin loginReq);

    RispostaUpdate update(RichiestaUpdate updateReq);
}
