package it.unina.bugboard.repository;

import it.unina.bugboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryUtente extends JpaRepository<User, Long> {
}
