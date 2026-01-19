package it.unina.bugboard.service;

import it.unina.bugboard.dto.RichiestaInserimentoCommentoIssue;
import it.unina.bugboard.dto.RispostaInserimentoCommentoIssue;
import it.unina.bugboard.entity.Issue;
import it.unina.bugboard.repository.CommentoRepository;
import it.unina.bugboard.repository.IssueRepository;
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
class CommentaIssueServiceImplTest {

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private CommentoRepository commentoRepository;

    @InjectMocks
    private CommentaIssueServiceImpl service;

    @Test
    void testInserisciCommento_SuIssueAperta_Successo() {
        // ARRANGE
        Integer issueId = 10;
        Long userId = 50L;

        // Simuliamo una Issue in stato "OPEN" (Commentabile)
        Issue issueAperta = new Issue();
        issueAperta.setIdIssue(issueId);
        issueAperta.setStato("OPEN");

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issueAperta));
        when(commentoRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        RichiestaInserimentoCommentoIssue richiesta = new RichiestaInserimentoCommentoIssue();
        richiesta.setIdIssue(issueId);
        richiesta.setTesto("Ecco un nuovo commento!");

        // ACT
        RispostaInserimentoCommentoIssue response = service.inserisciCommento(richiesta, userId);

        // ASSERT
        assertTrue(response.isSuccess(), "Il commento deve essere inserito con successo");
        verify(commentoRepository, times(1)).save(any());
    }

    @Test
    void testInserisciCommento_SuIssueChiusa_Fallimento() {

        Integer issueId = 10;

        Issue issueChiusa = new Issue();
        issueChiusa.setIdIssue(issueId);
        issueChiusa.setStato("CLOSED");

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issueChiusa));

        RichiestaInserimentoCommentoIssue richiesta = new RichiestaInserimentoCommentoIssue();
        richiesta.setIdIssue(issueId);
        richiesta.setTesto("Provo a commentare una issue chiusa...");

        // ACT
        RispostaInserimentoCommentoIssue response = service.inserisciCommento(richiesta, 50L);

        // ASSERT
        assertFalse(response.isSuccess(), "L'operazione deve fallire su issue chiusa");
        assertEquals("Non è possibile commentare una Issue chiusa", response.getMessage());
        verify(commentoRepository, never()).save(any());
    }

    @Test
    void testInserisciCommento_IssueNonTrovata_Fallimento() {
        // ARRANGE
        Integer issueId = 999;
        Long userId = 50L;

        when(issueRepository.findById(issueId)).thenReturn(Optional.empty());

        RichiestaInserimentoCommentoIssue richiesta = new RichiestaInserimentoCommentoIssue();
        richiesta.setIdIssue(issueId);
        richiesta.setTesto("Commento");

        // ACT
        RispostaInserimentoCommentoIssue response = service.inserisciCommento(richiesta, userId);

        // ASSERT
        assertFalse(response.isSuccess(), "Deve fallire se l'issue non esiste");
        assertEquals("Issue non trovata", response.getMessage());
        verify(commentoRepository, never()).save(any());
    }

    @Test
    void testInserisciCommento_IdIssueNull_Fallimento() {
        // ARRANGE
        Long userId = 50L;
        RichiestaInserimentoCommentoIssue richiesta = new RichiestaInserimentoCommentoIssue();
        richiesta.setIdIssue(null);
        richiesta.setTesto("Commento valido");

        // ACT
        RispostaInserimentoCommentoIssue response = service.inserisciCommento(richiesta, userId);

        // ASSERT
        assertFalse(response.isSuccess(), "Deve fallire se l'ID issue è null");
        assertEquals("ID Issue è obbligatorio", response.getMessage());
        verify(commentoRepository, never()).save(any());
    }

    @Test
    void testInserisciCommento_UtenteNonAutenticato_Fallimento() {
        // ARRANGE
        Integer issueId = 10;
        RichiestaInserimentoCommentoIssue richiesta = new RichiestaInserimentoCommentoIssue();
        richiesta.setIdIssue(issueId);
        richiesta.setTesto("Commento valido");

        RispostaInserimentoCommentoIssue response = service.inserisciCommento(richiesta, null);

        // ASSERT
        assertFalse(response.isSuccess(), "Deve fallire se l'utente non è autenticato");
        assertEquals("Utente non autenticato", response.getMessage());
        verify(commentoRepository, never()).save(any());
    }

    @Test
    void testInserisciCommento_TestoVuoto_Fallimento() {
        // ARRANGE
        Integer issueId = 10;
        Long userId = 50L;

        RichiestaInserimentoCommentoIssue richiesta = new RichiestaInserimentoCommentoIssue();
        richiesta.setIdIssue(issueId);
        richiesta.setTesto("   ");

        // ACT
        RispostaInserimentoCommentoIssue response = service.inserisciCommento(richiesta, userId);

        // ASSERT
        assertFalse(response.isSuccess(), "Deve fallire se il testo è vuoto");
        assertEquals("Il testo del commento non può essere vuoto", response.getMessage());
        verify(commentoRepository, never()).save(any());
    }
}
