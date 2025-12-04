package it.unina.bugboard.service;

import it.unina.bugboard.DTO.RichiestaLogin;
import it.unina.bugboard.DTO.RispostaLogin;

public interface LoginService {
    RispostaLogin login(RichiestaLogin loginReq);
}
