package it.unina.bugboard.repository;

import it.unina.bugboard.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface repositoryUtente extends JpaRepository<User, Long> {
    Optional<User> findByUsernameOrEmail(String username, String email);
}
