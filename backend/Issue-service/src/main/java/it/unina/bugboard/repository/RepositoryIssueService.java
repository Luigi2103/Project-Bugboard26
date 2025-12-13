package it.unina.bugboard.repository;

import it.unina.bugboard.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepositoryIssueService extends JpaRepository<Issue, Integer> {

    List<Issue> findByIdProgetto(int idProgetto);

    List<Issue> findByIdAssegnatario(int idAssegnatario);

    List<Issue> findByIdProgettoAndIdAssegnatario(int idProgetto, int idAssegnatario);

    @org.springframework.data.jpa.repository.Query("SELECT i FROM Issue i LEFT JOIN FETCH i.tags WHERE i.idAssegnatario = :idAssegnatario AND i.stato != :stato")
    List<Issue> findByIdAssegnatarioAndStatoNot(
            @org.springframework.web.bind.annotation.RequestParam("idAssegnatario") int idAssegnatario,
            @org.springframework.web.bind.annotation.RequestParam("stato") String stato);

}
