package it.unina.bugboard.controller;

import it.unina.bugboard.dto.RichiestaIssue;
import it.unina.bugboard.dto.RispostaIssue;
import it.unina.bugboard.service.InserimentoIssueService;
import it.unina.bugboard.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/Insertissue")
public class Controller {

    //private final InserimentoIssueService InsertService;

    //public Controller(InserimentoIssueService InsertService) { this.InsertService = InsertService; }

}
