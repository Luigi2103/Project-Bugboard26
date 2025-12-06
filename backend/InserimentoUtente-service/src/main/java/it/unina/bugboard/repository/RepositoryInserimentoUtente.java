package it.unina.bugboard.repository;

import it.unina.bugboard.Entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryInserimentoUtente extends JpaRepository<Utente, Long> {

    boolean existsByUsername(String username);
    boolean existsByMail(String email);
}
