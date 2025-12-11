package it.unina.bugboard.repository;

import it.unina.bugboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  RepositoryIssueService extends JpaRepository<User, Long> {

}
