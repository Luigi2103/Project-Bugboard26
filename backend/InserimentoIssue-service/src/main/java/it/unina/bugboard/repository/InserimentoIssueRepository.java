package it.unina.bugboard.repository;

import it.unina.bugboard.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InserimentoIssueRepository extends JpaRepository<Issue, Integer> {
}
