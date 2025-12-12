package it.unina.bugboard.repository;

import it.unina.bugboard.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  RepositoryIssueService extends JpaRepository<Issue, Long> {

}
