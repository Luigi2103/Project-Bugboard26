package it.unina.bugboard.service;

import it.unina.bugboard.dto.RichiestaModificaIssue;
import it.unina.bugboard.dto.RispostaModificaIssue;
import it.unina.bugboard.entity.Issue;
import it.unina.bugboard.entity.Priorita;
import it.unina.bugboard.repository.CronologiaRepository;
import it.unina.bugboard.repository.ModificaIssueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModificaIssueServiceImplTest {

    @Mock
    private ModificaIssueRepository repository;

    @Mock
    private CronologiaRepository cronologiaRepository;

    @InjectMocks
    private ModificaIssueServiceImpl service;

    @Test
    void testModificaIssue_CambioStato_Successo() {
        // ARRANGE
        Integer issueId = 1;
        Long userId = 100L;

        Issue issue = new Issue();
        issue.setIdIssue(issueId);
        issue.setStato("OPEN");

        when(repository.findById(issueId)).thenReturn(Optional.of(issue));
        when(repository.save(any(Issue.class))).thenAnswer(i -> i.getArguments()[0]);

        RichiestaModificaIssue richiesta = new RichiestaModificaIssue();
        richiesta.setStato("IN_PROGRESS");

        // ACT
        RispostaModificaIssue response = service.modificaIssue(issueId, richiesta, userId, "Mario", "Rossi");

        // ASSERT
        assertTrue(response.isSuccess());
        assertEquals("IN_PROGRESS", response.getIssue().getStato());
        verify(repository, times(1)).save(any(Issue.class));
        verify(cronologiaRepository, times(1)).save(any());
    }

    @Test
    void testModificaIssue_CambioStatoVuoto_Fallimento() {
        // ARRANGE
        Integer issueId = 1;
        Long userId = 100L;

        Issue issue = new Issue();
        issue.setIdIssue(issueId);
        issue.setStato("OPEN");

        when(repository.findById(issueId)).thenReturn(Optional.of(issue));

        RichiestaModificaIssue richiesta = new RichiestaModificaIssue();
        richiesta.setStato("");

        // ACT
        RispostaModificaIssue response = service.modificaIssue(issueId, richiesta, userId, "Mario", "Rossi");

        // ASSERT

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Stato non valido"));
        verify(repository, never()).save(any(Issue.class));
        verify(cronologiaRepository, never()).save(any());
    }

    @Test
    void testModificaIssue_IssueNonTrovata_Fallimento() {
        Integer idInesistente = 999;
        when(repository.findById(idInesistente)).thenReturn(Optional.empty());

        RichiestaModificaIssue richiesta = new RichiestaModificaIssue();
        richiesta.setStato("IN_PROGRESS");

        RispostaModificaIssue response = service.modificaIssue(idInesistente, richiesta, 100L, "Mario", "Rossi");

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().toLowerCase().contains("non trovata"));
        verify(repository, never()).save(any());
    }

    @Test
    void testModificaIssue_CambioPriorita_Successo() {
        Integer issueId = 1;
        Issue issue = new Issue();
        issue.setIdIssue(issueId);
        issue.setPriorita(Priorita.BASSA);

        when(repository.findById(issueId)).thenReturn(Optional.of(issue));
        when(repository.save(any(Issue.class))).thenAnswer(i -> i.getArguments()[0]);

        RichiestaModificaIssue richiesta = new RichiestaModificaIssue();
        richiesta.setPriorita("ALTA");

        RispostaModificaIssue response = service.modificaIssue(issueId, richiesta, 100L, "Mario", "Rossi");

        assertTrue(response.isSuccess());
        assertEquals(Priorita.ALTA.name(), response.getIssue().getPriorita());
        verify(cronologiaRepository, times(1)).save(any());
    }

    @Test
    void testModificaIssue_PrioritaInvalida_Fallimento() {
        Integer issueId = 1;
        Issue issue = new Issue();
        issue.setIdIssue(issueId);
        when(repository.findById(issueId)).thenReturn(Optional.of(issue));

        RichiestaModificaIssue richiesta = new RichiestaModificaIssue();
        richiesta.setPriorita("SUPER_URGENTE");

        RispostaModificaIssue response = service.modificaIssue(issueId, richiesta, 100L, "Mario", "Rossi");

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Priorità non valida"));
        verify(repository, never()).save(any());
    }

    @Test
    void testModificaIssue_PrioritaNulla_Fallimento() {
        Integer issueId = 1;
        Issue issue = new Issue();
        issue.setIdIssue(issueId);
        when(repository.findById(issueId)).thenReturn(Optional.of(issue));

        RichiestaModificaIssue richiesta = new RichiestaModificaIssue();
        richiesta.setPriorita("");

        RispostaModificaIssue response = service.modificaIssue(issueId, richiesta, 100L, "Mario", "Rossi");

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Priorità non valida"));
        verify(repository, never()).save(any());
    }

    @Test
    void testModificaIssue_CambioStatoEPriorita_Successo() {
        // ARRANGE
        Integer issueId = 1;
        Issue issue = new Issue();
        issue.setIdIssue(issueId);
        issue.setStato("OPEN");
        issue.setPriorita(Priorita.BASSA);

        when(repository.findById(issueId)).thenReturn(Optional.of(issue));
        when(repository.save(any(Issue.class))).thenAnswer(i -> i.getArguments()[0]);

        RichiestaModificaIssue richiesta = new RichiestaModificaIssue();
        richiesta.setStato("IN_PROGRESS");
        richiesta.setPriorita("ALTA");

        // ACT
        RispostaModificaIssue response = service.modificaIssue(issueId, richiesta, 100L, "Mario", "Rossi");

        // ASSERT
        assertTrue(response.isSuccess());
        assertEquals("IN_PROGRESS", response.getIssue().getStato());
        assertEquals(Priorita.ALTA.name(), response.getIssue().getPriorita());
        verify(repository, times(1)).save(any(Issue.class));
        verify(cronologiaRepository, times(2)).save(any());

    }

    @Test
    void testModificaIssue_NessunaModifica() {
        Integer issueId = 1;
        Issue issue = new Issue();
        issue.setIdIssue(issueId);
        issue.setStato("OPEN");
        issue.setPriorita(Priorita.MEDIA);

        when(repository.findById(issueId)).thenReturn(Optional.of(issue));

        RichiestaModificaIssue richiesta = new RichiestaModificaIssue();
        richiesta.setStato("OPEN");
        richiesta.setPriorita("MEDIA");

        RispostaModificaIssue response = service.modificaIssue(issueId, richiesta, 100L, "Mario", "Rossi");

        assertTrue(response.isSuccess());
        assertTrue(response.getMessage().contains("Nessuna modifica"));
        verify(repository, never()).save(any());
    }

}