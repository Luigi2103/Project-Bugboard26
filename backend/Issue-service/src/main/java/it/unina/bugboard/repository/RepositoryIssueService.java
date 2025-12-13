package it.unina.bugboard.repository;

import it.unina.bugboard.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RepositoryIssueService extends JpaRepository<Issue, Integer> {

    // Trova tutte le issue di un progetto ESCLUSE quelle CLOSED
    @Query("SELECT DISTINCT i FROM Issue i LEFT JOIN FETCH i.tags WHERE i.idProgetto = :idProgetto AND i.stato != 'CLOSED'")
    List<Issue> findByIdProgettoAndStatoNot(@Param("idProgetto") int idProgetto);

    // Trova le issue di un assegnatario ESCLUSE quelle CLOSED
    @Query("SELECT DISTINCT i FROM Issue i LEFT JOIN FETCH i.tags WHERE i.idAssegnatario = :idAssegnatario AND i.stato != 'CLOSED'")
    List<Issue> findByIdAssegnatarioAndStatoNot(@Param("idAssegnatario") int idAssegnatario);

    // Trova le issue di un progetto E di un assegnatario ESCLUSE quelle CLOSED
    @Query("SELECT DISTINCT i FROM Issue i LEFT JOIN FETCH i.tags WHERE i.idProgetto = :idProgetto AND i.idAssegnatario = :idAssegnatario AND i.stato != 'CLOSED'")
    List<Issue> findByIdProgettoAndIdAssegnatarioAndStatoNot(
            @Param("idProgetto") int idProgetto,
            @Param("idAssegnatario") int idAssegnatario);

    @Query("SELECT DISTINCT i FROM Issue i LEFT JOIN FETCH i.tags WHERE i.idIssue = :idIssue")
    Optional<Issue> findByIdWithTags(@Param("idIssue") Integer idIssue);
}