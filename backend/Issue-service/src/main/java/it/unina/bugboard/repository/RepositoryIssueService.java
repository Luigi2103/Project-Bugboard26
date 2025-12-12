package it.unina.bugboard.repository;

import it.unina.bugboard.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  RepositoryIssueService extends JpaRepository<Issue, Integer> {

    List<Issue> findByIdProgetto(int idProgetto);
    List<Issue> findByIdAssegnatario(int idAssegnatario);
    List<Issue> findByIdProgettoAndIdAssegnatario(int idProgetto, int idAssegnatario);

}
