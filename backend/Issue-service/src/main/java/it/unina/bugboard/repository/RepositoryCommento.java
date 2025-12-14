package it.unina.bugboard.repository;

import it.unina.bugboard.entity.Commento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryCommento extends JpaRepository<Commento, Long> {

    @Query("SELECT c FROM Commento c JOIN FETCH c.utente WHERE c.idIssue = :idIssue ORDER BY c.data ASC")
    List<Commento> findByIdIssueOrderByDataAsc(@Param("idIssue") Integer idIssue);
}