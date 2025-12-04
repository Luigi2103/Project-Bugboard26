package com.example.projectbugboard26.service;

import com.example.projectbugboard26.DTO.RichiestaLogin;
import com.example.projectbugboard26.DTO.RispostaLogin;

public interface LoginService {
    RispostaLogin login(RichiestaLogin loginReq);
}
